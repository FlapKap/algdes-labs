package gorilla;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
                var nameMatcher = Pattern.compile("^>([A-Za-z\\- ]+).*$").matcher(line);
                var matches = nameMatcher.matches(); //Java Patterns are super dumb.
                name = nameMatcher.group(1).trim();
                protein = new StringBuilder();
            }
        }
        species.addFirst(new Species(name, protein.toString()));
        return species;
    }
}
