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
        final Map<Integer, Map<Integer, Triplet<Integer, String, String>>> memoizer;

        public AlignmentEnvironment(String leftProtein, String rightProtein) {
            this.leftProtein = leftProtein;
            this.rightProtein = rightProtein;
            memoizer = new TreeMap<>();
            for (int i = 1; i < leftProtein.length() + 1; i++) {
                memoizer.put(i, new TreeMap<>());
            }
        }
    }

    private Triplet<Integer, String, String> align(int i, int j, AlignmentEnvironment env) {
        if (i == 0 && j == 0) {
            return new Triplet<>(0, "", "");
        }
        if (env.memoizer.containsKey(i) && env.memoizer.get(i).containsKey(j)) {
            return env.memoizer.get(i).get(j);
        }
        char GAP = '*';
        String GAP_S = GAP + "";

        if (i == 0) {
            var delta = costMatrix.getCost(env.rightProtein.charAt(j), GAP);
            return new Triplet<>(j * delta, GAP_S, env.rightProtein.charAt(j)+"");
        }
        if (j == 0) {
            var delta = costMatrix.getCost(env.leftProtein.charAt(i), GAP);
            return new Triplet<>(i * delta, env.leftProtein.charAt(i)+"", GAP_S);
        }
        var leftDelta = costMatrix.getCost(env.leftProtein.charAt(i), GAP);
        var rightDelta = costMatrix.getCost(env.rightProtein.charAt(j), GAP);

        var alpha = costMatrix.getCost(env.leftProtein.charAt(i), env.rightProtein.charAt(j));

        var leaveChars = align(i - 1, j - 1, env)
                .updateLeft(cost -> cost + alpha)
                .updateMiddle(l -> l + env.leftProtein.charAt(i))
                .updateRight(r -> r + env.rightProtein.charAt(j));

        var addGapLeft = align(i - 1, j, env)
                .updateLeft(cost -> cost + leftDelta)
                .updateMiddle(l -> l + GAP)
                .updateRight(r -> r + GAP);

        var addGapRight = align(i, j - 1, env)
                .updateLeft(cost -> cost + rightDelta)
                .updateMiddle(l -> l + GAP)
                .updateRight(r -> r + GAP);

        //Notice the odd arrangement of the statements.
        //This was done to match Thore's outputs.
        //My outputs were different, originally, if used (leaveChars.left >= Math.max(...)) in the first clause.
        //They were still right tho, but I thought that I would make it easy.
        if (leaveChars.left >= Math.max(addGapLeft.left, addGapRight.left)) {
            env.memoizer.get(i).put(j, leaveChars);
            return leaveChars;
        } else if (addGapLeft.left > Math.max(leaveChars.left, addGapRight.left)) {
            env.memoizer.get(i).put(j, addGapLeft);
            return addGapLeft;
        } else {
            env.memoizer.get(i).put(j, addGapRight);
            return addGapRight;
        }
    }

    private AlignedSequence align(Species left, Species right) {
        String leftProtein = " " + left.protein;
        String rightProtein = " " + right.protein;
        var startEnvironment = new AlignmentEnvironment(leftProtein, rightProtein);
        var alignment = align(left.protein.length(), right.protein.length(), startEnvironment);
        return new AlignedSequence(left, right, alignment.middle, alignment.right, alignment.left);
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
