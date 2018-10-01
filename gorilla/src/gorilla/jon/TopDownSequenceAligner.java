package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopDownSequenceAligner implements SequenceAligner {

    private static char GAP = '*';

    private CostMatrix costMatrix;

    private Pair<Character, String> consOfString(String s) {
        if (s.isEmpty()) {
            return new Pair<>(GAP, s);
        } else {
            return new Pair<>(s.charAt(0), (s.length() > 1) ? s.substring(1) : "");
        }
    }

    private Pair<String, Integer> align(String left, String right) {
        if (left.isEmpty() && right.isEmpty()) {
            return new Pair<>("", 0);
        }
        var leftCons = consOfString(left);
        var rightCons = consOfString(right);
        var x = leftCons.left;
        var xs = leftCons.right;
        var y = rightCons.left;
        var ys = rightCons.right;

        //Something is still not right here...
        var p1 = align(xs, right).updateRight((l, r) -> r + costMatrix.getCost(x, GAP));
        var p2 = align(left, ys).updateRight((l, r) -> r + costMatrix.getCost(y, GAP));
        var p3 = align(xs, ys).updateRight((l, r) -> r + costMatrix.getCost(x, y));

        return List.of(p1, p2, p3)
                .stream()
                .max(Comparator.comparingInt(pair -> pair.right))
                .orElseThrow();
    }

    @Override
    public List<AlignedSequence> alignSequences(CostMatrix costMatrix, List<Species> speciesList) {
        this.costMatrix = costMatrix;
        var alignedSequences = new ArrayList<AlignedSequence>(speciesList.size() * speciesList.size());
        for (int i = 0; i < speciesList.size(); i++) {
            for (int j = i + 1; j < speciesList.size(); j++) {
                var left = speciesList.get(i);
                var right = speciesList.get(j);
                var alignment = align(left.protein, right.protein);
                alignedSequences.add(new AlignedSequence(left, right, alignment.left, alignment.right));
            }
        }
        return alignedSequences;
    }
}
