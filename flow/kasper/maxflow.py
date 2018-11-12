import sys
import collections
import math

class Edge:

    def __init__(self, v, w, cap):
        self.v = v
        self.w = w
        self.cap = cap
        self.flow = 0

    def __str__(self):
        return f"Edge(v = {self.v}, w = {self.w}, cap = {self.cap}, flow = {self.flow})"

    def __repr__(self):
        return self.__str__()

    def other(self, vertex):
        return self.v if vertex == self.w else self.w

    def resCapTo(self, vertex):
        return self.flow if vertex == self.v else self.cap - self.flow

    def addResFlowTo(self, vertex, bottleneck):
        if vertex == self.v:
            self.flow -= bottleneck
        else:
            self.flow += bottleneck



def AugPath(G, s, t):
    seen = set([s]) # we have seen the root
    queue = collections.deque([s])
    path = {}
    while queue:
        vertex = queue.popleft()
        for neighbor in G[vertex]:
            other = neighbor.other(vertex)

            if neighbor.resCapTo(other) > 0 and other not in seen:
                path[other] = neighbor
                seen.add(other)
                queue.append(other)
    
    # produce path. It's going to be an array of pairs (vertex, edge) of the form [(t, edge),...,(<vertex from s>, edge)]
    # in words: Each pair is a vertex, and the edge traveling to that vertex. we travel from t to s.
    result = []
    curr = t
    while path.get(curr):
        result.append((curr, path[curr]))
        curr = path[curr].other(curr)

    return result, seen

def FordFulkerson(G, s, t):
    bottleneck = 2**32 # a very high int
    value = 0


    maxcap = 10**10

    for edges in G.values():
        for edge in edges:
            maxcap = min(maxcap, edge.cap)
    delta = 2**math.ceil(math.log2(maxcap))
    while delta >= 1:
        path, seen = AugPath(G,s,t)

        while t in seen:

            #find bottleneck
            for to, edge in path:
                bottleneck = min(bottleneck, edge.cap)

            for to, edge in path:
                edge.addResFlowTo(to, bottleneck)

            value += bottleneck
            path, seen = AugPath(G,s,t)
    return value


# n: num nodes
# m: num edges
# s: source node
# t: sink node
n, m, s, t = map(int, sys.stdin.readline().split())

network = {i: [] for i in range(n)}

for line in sys.stdin:
    v, w, cap = map(int, line.split())
    network[v].append(
        Edge(v, w, cap)
    )

maxflow = FordFulkerson(network, s, t)

solution = []
for edges in network.values():
    for edge in filter(lambda x: x.flow > 0, edges):
        solution.append(edge)

print(f"{n} {maxflow} {len(solution)}")
for edge in solution:
    print(f"{edge.v} {edge.w} {edge.flow}")