package gorilla;

import util.jon.InputReader;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class CostMatrixParser {
    public static CostMatrix parseCostMatrix(InputStream stream) {
        final var inputReader = new InputReader(stream);
        final var header = inputReader
                .findNext("[^#]\\s+(\\w.*)")
                .get(1)
                .split("\\s+");
        final var n = header.length;
        final var mappings = new TreeMap<Character, Map<Character, Integer>>();
        while (inputReader.ready()) {
            final var row = inputReader.findNext("(^[A-Z\\*].*)").get(1);
            final var split = row.split("\\s+");
            final var fromCharacter = split[0].trim().charAt(0);
            mappings.put(fromCharacter, new TreeMap<>());
            for (int i = 0; i < n; i++) {
                final var toCharacter = header[i].trim().charAt(0);
                final var cost = Integer.parseInt(split[i + 1]);
                mappings.get(fromCharacter).put(toCharacter, cost);
            }
        }
        return new CostMatrix(mappings);
    }
}
