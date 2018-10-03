package gorilla.jon;

import gorilla.AlignedSequence;
import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import util.jon.Pair;

import java.util.*;
import java.util.function.Function;

public class BrutishSequenceAligner implements SequenceAligner {

    Map<Integer, Map<Integer, Pair<Character, Integer>>> memoizer;
    CostMatrix costMatrix;
    String src;
    String dest;

    private Pair<Character, Integer> optimize(int i, int j) {
        if (i == 0 || j == 0) {
            return new Pair<>('*', 0);
        }
        return List.of(
                memoizer.computeIfAbsent(i, i0 -> new TreeMap<>()).computeIfAbsent(
                        j - 1,
                        (j0) -> optimize(i -1, j0).updateRight((chr, cost) ->
                                cost + costMatrix.getCost(src.charAt(i), src.charAt(j)))
                ),
                memoizer.computeIfAbsent(i - 1, (i1) -> new TreeMap<>()).computeIfAbsent(
                        j,
                        (j1) -> optimize(i - 1, j1).update((chr, cost) ->
                                new Pair<>('*', cost + costMatrix.getCost(chr, '*')))
                ),
                memoizer.computeIfAbsent(i, i2 -> new TreeMap<>()).computeIfAbsent(
                        j - 1,
                        (j2) -> optimize(i, j2).update((chr, cost) ->
                                new Pair<>('*', cost + costMatrix.getCost(chr, '*')))
                )
        ).stream().max(Comparator.comparingInt(p -> p.right)).orElseThrow();
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
                int cost = 0;
                var result = new StringBuilder();
                memoizer = new TreeMap<>();
                src = left.protein;
                dest = right.protein;
                for (int i = 1; i < left.protein.length(); i++) {
                    for (int j = 1; j < left.protein.length(); j++) {
                        final var i1 = j;
                        var optC = memoizer.computeIfAbsent(i, i0 -> new TreeMap<>()).computeIfAbsent(j, (j1) -> optimize(i1, j1));
                        result.append(optC.left);
                        cost += optC.right;
                    }
                }
                alignedSequences.add(new AlignedSequence(left, right, result.toString(), cost));
            }
        }
        return alignedSequences;
    }
}
