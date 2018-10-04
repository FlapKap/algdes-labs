package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;
import util.jon.Triplet;
import util.jon.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.List.*;

public class TopDownSequenceAligner implements SequenceAligner {

    private CostMatrix costMatrix;

    private class AlignmentEnvironment {
        final String leftProtein;
        final String rightProtein;
        final Map<Integer, Map<Integer, Pair<Integer, String>>> memoizer;

        public AlignmentEnvironment(String leftProtein, String rightProtein) {
            this.leftProtein = leftProtein;
            this.rightProtein = rightProtein;
            memoizer = new TreeMap<>();
            for (int i = 1; i < leftProtein.length() + 1; i++) {
                memoizer.put(i, new TreeMap<>());
            }
        }
    }

    private Pair<Integer, String> align(int i, int j, AlignmentEnvironment env) {
        if (i == 0 && j == 0) {
            return new Pair<>(0, "");
        }
        if (env.memoizer.containsKey(i) && env.memoizer.get(i).containsKey(j)) {
            return env.memoizer.get(i).get(j);
        }
        char GAP = '*';
        String GAP_S = GAP + "";

        if (i == 0) {
            var delta = costMatrix.getCost(env.rightProtein.charAt(j), GAP);
            return new Pair<>(j * delta, GAP_S);
        }
        if (j == 0) {
            var delta = costMatrix.getCost(env.leftProtein.charAt(i), GAP);
            return new Pair<>(i * delta, GAP_S);
        }
        var leftDelta = costMatrix.getCost(env.leftProtein.charAt(i), GAP);
        var rightDelta = costMatrix.getCost(env.rightProtein.charAt(j), GAP);

        var alpha = costMatrix.getCost(env.leftProtein.charAt(i), env.rightProtein.charAt(j));

        var leaveChars = align(i - 1, j - 1, env).updateLeft(cost -> cost + alpha).updateRight(l -> l + env.leftProtein.charAt(i));

        var addGapLeft = align(i - 1, j, env).updateLeft(cost -> cost + leftDelta).updateRight(l -> l + GAP);

        var addGapRight = align(i, j - 1, env).updateLeft(cost -> cost + rightDelta).updateRight(l -> l + GAP);

        //Notice the odd arrangement of the statements.
        //This was done to match Thore's outputs.
        //My outputs were different, originally, if used (leaveChars.left >= Math.max(...)) in the first clause.
        //They were still right tho, but I thought that I would make it easy.
        if (leaveChars.left > Math.max(addGapLeft.left, addGapRight.left)) {
            env.memoizer.get(i).put(j, leaveChars);
            return leaveChars;
        } else if (addGapLeft.left >= Math.max(leaveChars.left, addGapRight.left)) {
            env.memoizer.get(i).put(j, addGapLeft);
            return addGapLeft;
        } else {
            env.memoizer.get(i).put(j, addGapRight);
            return addGapRight;
        }
    }

    private Pair<Integer, String> align(String source, String destination) {

        String leftProtein = " " + source;
        String rightProtein = " " + destination;

        return align(source.length(), destination.length(), new AlignmentEnvironment(leftProtein, rightProtein));
    }

    private AlignedSequence align(Species left, Species right) {
        var alignLeft = align(left.protein, right.protein);
        var alignRight = align(right.protein, left.protein);
        if (!alignLeft.left.equals(alignRight.left)) {
            throw new IllegalStateException("It should not be possible to get two different optimal costs.");
        }
        return new AlignedSequence(left, right, alignLeft.right, alignRight.right, alignLeft.left);
    }

    @Override
    public List<AlignedSequence> alignSequences(CostMatrix costMatrix, List<Species> speciesList) {
        this.costMatrix = costMatrix;
        List<Pair<Species, Species>> pairings = new ArrayList<>(speciesList.size() * speciesList.size());
        for (var left : speciesList) {
            for (var right : speciesList) {
                if (left.equals(right)) {
                    continue;
                }
                var pair = Pair.of(left, right);
                if (pairings.stream().noneMatch(pair::equivalent)) {
                    pairings.add(pair);
                }
            }
        }
        return pairings.stream()
                .distinct()
                .parallel()
                .map(p -> align(p.left, p.right))
                .collect(Collectors.toList());
    }
}
