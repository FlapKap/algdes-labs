package gorilla;

import gorilla.jon.TopDownSequenceAligner;
import util.jon.MainHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class Main {

    private static void printAlignedSequences(List<AlignedSequence> alignedSequences) {
        for (var seq : alignedSequences) {
            System.out.printf(
                    "%s--%s: %d\n%s\n%s\n",
                    seq.source.name,
                    seq.destination.name,
                    seq.cost,
                    seq.rightAlign.replace('*', '-'),
                    seq.leftAlign.replace('*', '-')
            );
        }
    }

    private static BiConsumer<String, InputStream> runForFile(CostMatrix costMatrix) {
        return (filename, stream) -> {
            System.out.printf("Parsing '%s'...\n", filename);
            var species = SpeciesParser.parseSpecies(stream);
            System.out.printf("Parsed %d species!\n", species.size());

            var sequenceAligner = new TopDownSequenceAligner(); //Replace with whatever implementation you use.
                                                                //Or make your own main method!

            var alignedSeqs = sequenceAligner.alignSequences(costMatrix, species);

            printAlignedSequences(alignedSeqs);
        };
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2) {
            System.out.println(
                    "Usage of this program: java Main <Path to cost matrix file> <One or more paths to leftAlignment files>"
            );
            return;
        }
        final var start = System.currentTimeMillis();
        System.out.printf("Parsing CostMatrix '%s'\n", args[0]);
        final var costMatrix = CostMatrixParser.parseCostMatrix(new FileInputStream(args[0]));
        MainHelper.acceptArgs(Arrays.copyOfRange(args, 1, args.length), runForFile(costMatrix));
        System.out.printf("Total elapsed time: %dms\n", System.currentTimeMillis() - start);
    }
}
