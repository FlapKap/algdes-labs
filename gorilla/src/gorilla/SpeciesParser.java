package gorilla;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import util.jon.InputReader;

public class SpeciesParser {
    public static List<Species> parseSpecies(InputStream stream) {
        final var reader = new InputReader(stream);
        final var species = new LinkedList<Species>();
        String name = null;
        StringBuilder protein = new StringBuilder();
        while (reader.ready()) {
            var line = reader.readLine();
            if (!line.startsWith(">")) {
                protein.append(line);
            } else {
                if (name != null) {
                    species.addFirst(new Species(name, protein.toString()));
                }
                name = line.split("\\W")[1];
                protein = new StringBuilder();
            }
        }
        return species;
    }
}
