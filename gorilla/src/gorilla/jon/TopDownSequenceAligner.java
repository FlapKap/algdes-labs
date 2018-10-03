package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;
import util.jon.Triplet;

import java.util.*;

public class TopDownSequenceAligner implements SequenceAligner {

    private CostMatrix costMatrix;

    private Map<Integer, Map<Integer, Triplet<String, String, Integer>>> memoizer;

    private String leftProtein;
    private String rightProtein;

    public Triplet<String, String, Integer> align(int i, int j) {
        if (i == 0 && j == 0) {
            return new Triplet<>("", "", 0);
        }
        if (memoizer.containsKey(i) && memoizer.get(i).containsKey(j)) {
            return memoizer.get(i).get(j);
        }
        char GAP = '*';

        if (i == 0) {
            var delta = costMatrix.getCost(rightProtein.charAt(j), GAP);
            return new Triplet<>("", "" + GAP, j * delta);
        }
        if (j == 0) {
            var delta = costMatrix.getCost(leftProtein.charAt(i), GAP);
            return new Triplet<>("" + GAP, "", i * delta);
        }
        var leftDelta = costMatrix.getCost(leftProtein.charAt(i), GAP);
        var rightDelta = costMatrix.getCost(rightProtein.charAt(j), GAP);

        var alpha = costMatrix.getCost(leftProtein.charAt(i), rightProtein.charAt(j));
        var leaveChars = align(i - 1, j - 1).updateRight(cost -> cost + alpha);

        var addGapLeft = align(i - 1, j).updateRight(cost -> cost + leftDelta);

        var addGapRight = align(i, j - 1).updateRight(cost -> cost + rightDelta);

        if (leaveChars.right >= Math.max(addGapLeft.right, addGapRight.right)) {
            var leftChars = leaveChars.update(tri -> new Triplet<>(
                    tri.left + leftProtein.charAt(i),
                    tri.middle + leftProtein.charAt(j),
                    tri.right)
            );
            memoizer.get(i).put(j, leftChars);
            return leftChars;
        } else if (addGapLeft.right >= Math.max(leaveChars.right, addGapRight.right)) {
            var addedGapLeft = addGapLeft.update(tri -> new Triplet<>(
                    tri.left + GAP,
                    tri.middle + rightProtein.charAt(j),
                    tri.right)
            );
            memoizer.get(i).put(j, addedGapLeft);
            return addedGapLeft;
        } else {
            var addedGapRight = addGapRight.update(tri -> new Triplet<>(
                    tri.left + leftProtein.charAt(i),
                    tri.middle + GAP,
                    tri.right)
            );
            memoizer.get(i).put(j, addedGapRight);
            return addedGapRight;
        }
    }

    public AlignedSequence align(Species left, Species right) {
        memoizer = new TreeMap<>();
        for (int i = 1; i < left.protein.length() + 1; i++) {
            memoizer.put(i, new TreeMap<>());
        }
        leftProtein = " " + left.protein;
        rightProtein = " " + right.protein;
        var result = align(left.protein.length(), right.protein.length());
        return new AlignedSequence(left, right, result.left, result.middle, result.right);
    }

    @Override
    public List<AlignedSequence> alignSequences(CostMatrix costMatrix, List<Species> speciesList) {
        this.costMatrix = costMatrix;
        var alignedSequences = new ArrayList<AlignedSequence>(speciesList.size() * speciesList.size());
        for (var left : speciesList) {
            for (var right : speciesList) {
                if (left.equals(right)) {
                    continue;
                }
                alignedSequences.add(align(left, right));
            }
        }
        return alignedSequences;
    }
}
