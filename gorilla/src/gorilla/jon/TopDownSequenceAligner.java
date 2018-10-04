package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;
import util.jon.Triplet;
import util.jon.Utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.List.*;

public class TopDownSequenceAligner implements SequenceAligner {

    private CostMatrix costMatrix;

    private Map<Integer, Map<Integer, Pair<Integer, String>>> memoizer;

    private String leftProtein;
    private String rightProtein;

    public enum SwitchMode {
        LEAVE_LEFT_RIGHT,
        LEAVE_RIGHT_LEFT,
        LEFT_LEAVE_RIGHT,
        LEFT_RIGHT_LEAVE,
        RIGHT_LEFT_LEAVE,
        RIGHT_LEAVE_LEFT
    }

    private SwitchMode switchMode;

    public TopDownSequenceAligner(SwitchMode switchMode) {
        this.switchMode = switchMode;
    }

    public TopDownSequenceAligner() {
        this(SwitchMode.LEAVE_LEFT_RIGHT);
    }

    private Pair<Integer, String> align(int i, int j) {
        if (i == 0 && j == 0) {
            return new Pair<>(0, "");
        }
        if (memoizer.containsKey(i) && memoizer.get(i).containsKey(j)) {
            return memoizer.get(i).get(j);
        }
        char GAP = '*';
        String GAP_S = GAP + "";

        if (i == 0) {
            var delta = costMatrix.getCost(rightProtein.charAt(j), GAP);
            return new Pair<>(j * delta, GAP_S);
        }
        if (j == 0) {
            var delta = costMatrix.getCost(leftProtein.charAt(i), GAP);
            return new Pair<>(i * delta, GAP_S);
        }
        var leftDelta = costMatrix.getCost(leftProtein.charAt(i), GAP);
        var rightDelta = costMatrix.getCost(rightProtein.charAt(j), GAP);

        var alpha = costMatrix.getCost(leftProtein.charAt(i), rightProtein.charAt(j));

        var leaveChars = align(i - 1, j - 1).updateLeft(cost -> cost + alpha).updateRight(l -> l + leftProtein.charAt(i));

        var addGapLeft = align(i - 1, j).updateLeft(cost -> cost + leftDelta).updateRight(l -> l + GAP);

        var addGapRight = align(i, j - 1).updateLeft(cost -> cost + rightDelta).updateRight(l -> l + GAP);

        Function<
                Triplet<
                        Pair<Integer, String>,
                        Pair<Integer, String>,
                        Pair<Integer, String>
                >,
                Pair<Integer, String>
        > takeMaxCase = (triplet) -> {
            final var first = triplet.left;
            final var second = triplet.middle;
            final var third = triplet.right;
            if (first.left >= Math.max(addGapLeft.left, addGapRight.left)) {
                memoizer.get(i).put(j, first);
                return first;
            } else if (second.left >= Math.max(first.left, third.left)) {
                memoizer.get(i).put(j, second);
                return second;
            } else {
                memoizer.get(i).put(j, third);
                return third;
            }
        };

        switch(switchMode) {
            case LEAVE_LEFT_RIGHT:
                return takeMaxCase.apply(Triplet.of(leaveChars, addGapLeft, addGapRight));
            case LEAVE_RIGHT_LEFT:
                return takeMaxCase.apply(Triplet.of(leaveChars, addGapRight, addGapLeft));
            case LEFT_LEAVE_RIGHT:
                return takeMaxCase.apply(Triplet.of(addGapLeft, leaveChars, addGapRight));
            case LEFT_RIGHT_LEAVE:
                return takeMaxCase.apply(Triplet.of(addGapLeft, addGapRight, leaveChars));
            case RIGHT_LEAVE_LEFT:
                return takeMaxCase.apply(Triplet.of(addGapRight, leaveChars, addGapLeft));
            default:
                return takeMaxCase.apply(Triplet.of(addGapRight, addGapLeft, leaveChars));
        }
    }

    private Pair<Integer, String> align(String source, String destination) {
        memoizer = new TreeMap<>();
        for (int i = 1; i < source.length() + 1; i++) {
            memoizer.put(i, new TreeMap<>());
        }
        leftProtein = " " + source;
        rightProtein = " " + destination;

        var result = align(source.length(), destination.length());
        var cost = result.left;
        StringBuilder alignedProtein = new StringBuilder(result.right);

        //Pad the protein to fit size of destination Species protein.
        var maxLength = Math.max(source.length(), destination.length());
        for (int i = alignedProtein.length(); i < maxLength; i++) {
            alignedProtein.insert(0, "*");
        }
        return new Pair<>(cost, alignedProtein.toString());
    }

    private AlignedSequence align(Species left, Species right) {
        var alignLeft = align(left.protein, right.protein);
        var alignRight = align(right.protein, left.protein);
        if (!alignLeft.left.equals(alignRight.left)) {
            throw new IllegalStateException("It should not be possible to get to different optimal costs.");
        }
        return new AlignedSequence(left, right, alignLeft.right, alignRight.right, alignLeft.left);
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
        alignedSequences.sort(Comparator.comparing(a -> a.source.name));
        return alignedSequences;
    }
}
