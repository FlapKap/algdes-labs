from pathlib import Path
import argparse
import sys
from itertools import combinations
from functools import lru_cache
from math import ceil, floor
data_folder = Path("../../../data/")

#region
# First we do parsing!
# First read the costs
costs = {}
with open( data_folder / "BLOSUM62.txt" ) as f:
    mapping = []
    for line in f.readlines():
        # skip all comment lines
        if line.startswith("#"):
            continue
        
        # now create horizontal letter mapping and initialize costs
        if len(mapping) == 0:
            mapping = line.strip().split()
            costs = {letter: {} for letter in mapping} # for every letter in mapping, create a <letter>: emptyDict in the cost dictionary
            continue
        
        # all lines we see from this point on are now the rows in the matrix
        letter1, *values = line.split() # get the first entry as the letter and the rest as a list
        values = map(lambda x: -1* int(x), values) # convert the list of strings to ints

        # save the cost under their appropriate spot in the costs dictionary (of dictionaries)
        for letter2, value in zip(mapping, values): # zip creates pairs of the values in mapping and values. Example: zip(["A", "B"], [1,2]) = [("A", 1)("B", 2)]
            costs[letter1][letter2] = value

gap = costs['A']['*'] # just pick whatever gap

# here we read commandline input as either a file or a stream. we dont care
parser = argparse.ArgumentParser(description='do some nifty shit')
parser.add_argument('input', type=argparse.FileType(), default=sys.stdin)

sequencefile = parser.parse_args().input

sequences = {} # this will hold our sequences, with their pairing as the name
for line in sequencefile.readlines():

    if line.startswith(">"):
        name = line.split()[0][1:] # the [1:] is to remove the >
        sequences[name] = ""
        continue

    sequences[name] += line.strip()

#endregion

#region
# Alignment algorithms

#The first is our plain-old alignment
@lru_cache(maxsize=None)
def alignment(X,Y):

    # base case: one of the strings is empty. The cost is the length of the other string times gap penalty
    # the alignment of the empty string is "-" times the length of the other string
    if len(X) == 0:
        return (len(Y) * gap, "-" * len(Y), Y)
    if len(Y) == 0:
        return (len(X) * gap, X,"-" * len(X))


    # recurrance cases: 
    # case1: we match the last letter in X with the last letter in Y 
    # note array[-1] is a shorthand for array[len(array) - 1], so it just takes the last element
    scoreV, AV, BV = alignment(X[:-1], Y[:-1])
    scoreV += costs[X[-1]][Y[-1]]
    AV = f"{AV}{X[-1]}" # The matchings are whatever matches with the earlier string, plus the current letter
    BV = f"{BV}{Y[-1]}"

    # case2: we match the last letter in X with a gap
    scoreL, AL, BL = alignment(X[:-1], Y)
    scoreL += gap
    AL = f"{AL}{X[-1]}"
    BL = f"{BL}-"

    # case3: we match the last letter in Y with a gap
    scoreD, AD, BD = alignment(X, Y[:-1])
    scoreD += gap
    AD = f"{AD}-"
    BD = f"{BD}{Y[-1]}"

    score = min(scoreV, scoreL, scoreD)

    if score == scoreV:
        return score, AV, BV
    if score == scoreL:
        return score, AL, BL
    if score == scoreD:
        return score, AD, BD


# divide and conquer
# requires 2 algorithms, forward and backward alignment

# find and return the last column of the costs for the 2 strings
def forwardAlignment(X, Y):
    previous = [gap * index for index in range(len(X) + 1)] # first column is always gaps
    current = [0] * (len(X) + 1)

    for j, y  in enumerate(Y, 1):
        current[0] = j * gap
        for i, x in enumerate(X, 1):
            current[i] = min(
                costs[x][y] + previous[i - 1],
                gap + current[i - 1],
                gap + previous[i]
            )
        previous = current.copy()

    return current

# the reverse of forward alignment ([::-1] reverses arrays). This is to find the costs from the beginning of the strings instead of the end
def backwardAlignment(X, Y):
    return reversed(forwardAlignment(X[::-1], Y[::-1]))


def divideAndConquerAlignment(X, Y):
    m = len(X)
    n = len(Y)
    if m <= 2 or n <= 2:
        return alignment(X, Y)

    prefix = forwardAlignment(X, Y[:n//2])
    suffix = backwardAlignment(X, Y[(n//2):])

    total_cost = [start + end for start, end in zip(prefix, suffix)]
    q = total_cost.index(min(total_cost))
    
    # we dont care about P since we get the string representations of the alignments from the base case
    # P is essentially the same as the 2 strings in the return
    #P.append((q, n // 2))
    scorePre, aPre, bPre = divideAndConquerAlignment(X[:q], Y[:n//2])
    scoreSuf, aSuf, bSuf = divideAndConquerAlignment(X[q:], Y[n//2:]) 
    # note we do not +1 the ranges here, since // always rounds down and the array-range is incl. exclusive
    return (scorePre + scoreSuf, aPre + aSuf, bPre + bSuf)


# this is just some test code to verify results
import re
text = open("..\..\..\data\HbB_FASTAs-out.txt").read()

def verifyresult(name1, name2, score):
    first = re.escape(name1)
    second = re.escape(name2)
    regex = r"^(?:(?:(?:"+first+r"\-\-"+second+r")|(?:"+second+r"\-\-"+first+r")):\s+)(?P<score>\d+)$"
    m = re.search(regex, text,re.MULTILINE)
    return int(m.group('score')) == -1 * score


# and here is the main loop of the program. Combinations in this case makes unique combinations of all sequences
for (name1, seq1), (name2, seq2) in combinations(sequences.items(), 2):
    score, a, b = divideAndConquerAlignment(seq1, seq2)
    print(f"{name1}--{name2}: {score}")
    print(a)
    print(b)

