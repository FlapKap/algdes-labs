package gorilla.jon;

import gorilla.*;
import org.junit.jupiter.api.Test;
import util.jon.InputReader;
import util.jon.Pair;
import util.jon.Triplet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TopDownSequenceAlignerTest {

    /*
        We have a tiny range of amino-acids:
        A B and the gap character *.
        We use the cost matrix:

              A  B  *
            A 0  -1 -4
            B -1 0  -2
            * -4 -2 0
     */
    private final CostMatrix simpleCostMatrix = new CostMatrix(
            Map.of(
                    'A', Map.of('A', 0, 'B', -1, 'C', -8, '*', -4),
                    'B', Map.of('A', -1, 'B', 0, 'C', -1, '*', -2),
                    'C', Map.of('A', -8, 'B', -2, 'C', 0, '*', -2),
                    '*', Map.of('A', -4, 'B', -2, 'C', -2, '*', 0)
            )
    );

    private final SequenceAligner testAligner = new TopDownSequenceAligner();

    @Test
    void alignSequencesTest() {
        /*
            Let's work with an example of two tiny species:
            s0: AB
            s1: BB

            Aligning s0 with s1 should yield the result:
            BB, with the cost of -1

            Aligning s1 with s0 should yield the result:
            AB, with the cost of -1

            destination?
         */
        var species = List.of(new Species("s0", "AB"), new Species("s1", "BB"));

        var alignedSeqs = testAligner.alignSequences(simpleCostMatrix, species);

        assertTrue(
                alignedSeqs.stream().anyMatch(as ->
                        as.source.name.equals("s0") &&
                                (as.leftAlign.equals("BB") || as.rightAlign.equals("BB")) &&
                                as.cost.equals(-1)
                ),
                "Aligned sequences contain s0-s1 -> AB, -1"
        );
        assertTrue(
                alignedSeqs.stream().anyMatch(as ->
                        as.source.name.equals("s1") &&
                                (as.leftAlign.equals("AB") || as.rightAlign.equals("AB")) &&
                                as.cost.equals(-1)
                ),
                "Aligned sequences contain s1-s0 -> BB, -1"
        );
    }

    private String getShortName(Species species) {
        return species.name.split("\\s+")[0];
    }
    private Pair<Pair<String, String>, Pair<String, String>> getShortNameCombinations(AlignedSequence aSeq) {
        return Pair.of(
                Pair.of(getShortName(aSeq.source), getShortName(aSeq.destination)),
                Pair.of(getShortName(aSeq.destination), getShortName(aSeq.source))
        );
    }

    private Predicate<AlignedSequence> aSeqInOutput (Map<Pair<String, String>, Triplet<Integer, String, String>> oA) {
        return (aSeq -> {
            final var combinations = getShortNameCombinations(aSeq);
            if (!oA.containsKey(combinations.left) || !oA.containsKey(combinations.right)) {
                return true;
            }
            final var value = (oA.containsKey(combinations.left)) ? oA.get(combinations.left) : oA.get(combinations.right);
            return aSeq.cost.equals(value.left) &&
                    Math.max(
                            aSeq.leftAlign.length(),
                            aSeq.rightAlign.length()
                    ) <= Math.max(
                            aSeq.source.protein.length(),
                            aSeq.destination.protein.length()
                    );
        });
    }

    @Test
    void compareAlignmentToGivenOutput() throws FileNotFoundException {
        var outputStream = new FileInputStream("./gorilla/data/HbB_FASTAs-out.txt");
        var outputReader = new InputReader(outputStream);
        var outputAlignments = new HashMap<Pair<String, String>, Triplet<Integer, String, String>>();

        while (outputReader.ready()) {
            var match = outputReader.findNext("^(\\w+)--(\\w+): (\\d+).*$");
            var names = new Pair<>(match.get(1), match.get(2));
            var cost = Integer.parseInt(match.get(3));
            var seqPat = Pattern.compile("^([A-Z\\-]+)$");
            var leftSeq = outputReader.findNext(seqPat).get(1);
            var rightSeq = outputReader.findNext(seqPat).get(1);
            outputAlignments.put(names, new Triplet<>(cost, leftSeq, rightSeq));
        }

        var matrixStream = new FileInputStream("./gorilla/data/BLOSUM62.txt");
        var matrix = CostMatrixParser.parseCostMatrix(matrixStream);
        var speciesStream = new FileInputStream("./gorilla/data/HbB_FASTAs-in.txt");
        var species = SpeciesParser.parseSpecies(speciesStream);
        var alignedSequences = testAligner.alignSequences(matrix, species);

        //TODO: Finish implementing this test.
        assertTrue(alignedSequences.stream()
                .allMatch(aSeqInOutput(outputAlignments)),
                "Every alignedSequence matches the costs of the output file.");
    }
}