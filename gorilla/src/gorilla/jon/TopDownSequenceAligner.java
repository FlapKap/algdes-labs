package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;
import util.jon.Triplet;

import java.util.*;

public class TopDownSequenceAligner implements SequenceAligner {

    private static char GAP = '*';

    private CostMatrix costMatrix;

    private Map<Integer, Pair<String, Integer>> memoizer;

    public TopDownSequenceAligner() {
        memoizer = new HashMap<>();
    }

    private Pair<Character, String> consOfString(String s) {
        if (s.isEmpty()) {
            return new Pair<>(GAP, s);
        } else {
            return new Pair<>(s.charAt(0), (s.length() > 1) ? s.substring(1) : "");
        }
    }

    private Pair<String, Integer> align(Pair<String, Integer> acc, String left, String right) {
        int inputHash = Objects.hash(acc, left, right);
        if (memoizer.containsKey(inputHash)) {
            return memoizer.get(inputHash);
        }
        if (left.isEmpty() && right.isEmpty()) {
            return acc;
        }
        var leftCons = consOfString(left);
        var rightCons = consOfString(right);
        var x = leftCons.left;
        var xs = leftCons.right;
        var y = rightCons.left;
        var ys = rightCons.right;

        //Something is still not right here...
        var p1 = align(new Pair<>(acc.left + GAP, acc.right + costMatrix.getCost(x, GAP)), xs, ys);
        var p2 = align(new Pair<>(acc.left + GAP, acc.right + costMatrix.getCost(y, GAP)), xs, ys );
        var p3 = align(new Pair<>(acc.left + x, acc.right + costMatrix.getCost(x, y)), xs, ys);

        var opt = List.of(p1, p2, p3)
                .stream()
                .max(Comparator.comparingInt(pair -> pair.right))
                .orElseThrow();
        memoizer.put(inputHash, opt);
        return opt;
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
                var alignment = align(new Pair<>("", 0), left.protein, right.protein);
                alignedSequences.add(new AlignedSequence(left, right, alignment.left, alignment.right));
            }
        }
        return alignedSequences;
    }
}
