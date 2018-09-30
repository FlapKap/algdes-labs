package gorilla.jon;

import util.jon.MainHelper;
import java.io.InputStream;

public class Main {

    private static void runForFile(String filename, InputStream stream) {
        System.out.printf("Parsing '%s'...\n", filename);
        var species = SpeciesParser.parseSpecies(stream);
        System.out.printf("Parsed %d species!\n", species.size());

    }

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();
        MainHelper.acceptArgs(args, Main::runForFile);
        System.out.printf("Total elapsed time: %dms\n", System.currentTimeMillis() - start);
    }
}
