package gorilla.jon;

import gorilla.CostMatrix;
import gorilla.SequenceAligner;
import gorilla.Species;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BrutishSequenceAlignerTest {

    /*
        We have a tiny range of amino-acids:
        A B and the gap character *.
        We use the cost matrix:

              A  B  *
            A 0  -1 -4
            B -1 0  -2
            * -4 -2 0
     */
    CostMatrix costMatrix = new CostMatrix(
            Map.of(
                    'A', Map.of('A', 0, 'B', -1, '*', -4),
                    'B', Map.of('A', -1, 'B', 0, '*', -2),
                    '*', Map.of('A', -4, 'B', -2, '*', 0)
            )
    );

    SequenceAligner testAligner = new BrutishSequenceAligner();

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

            right?
         */
        var species = List.of(new Species("s0", "AB"), new Species("s1", "BB"));

        var alignedSeqs = testAligner.alignSequences(costMatrix, species);

        assertTrue(
                alignedSeqs.stream().anyMatch(as ->
                        as.left.name.equals("s0") &&
                                as.sequence.equals("BB") &&
                                as.cost.equals(-1)
                ),
                "Aligned sequences contain s0-s1 -> AB, -1"
        );
        assertTrue(
                alignedSeqs.stream().anyMatch(as ->
                        as.left.name.equals("s1") &&
                                as.sequence.equals("AB") &&
                                as.cost.equals(-1)
                ),
                "Aligned sequences contain s1-s0 -> BB, -1"
        );
    }
}