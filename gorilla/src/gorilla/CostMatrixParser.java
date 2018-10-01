package gorilla;

import util.jon.InputReader;
import util.jon.Pair;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class CostMatrixParser {
    public static Map<Pair<Character, Character>, Integer> parseCostMatrix(InputStream stream) {
        final var inputReader = new InputReader(stream);
        final var outputMap = new TreeMap<Pair<Character, Character>, Integer>();
        final var header = inputReader.findNext("([^#].*)").get(1);
        while (inputReader.ready()) {
            final var row = inputReader.findNext("(^[A-Z\\*].*)").get(1);
            final var split = row.split(" ");
            throw new RuntimeException("NOT IMPLEMENTED");
        }
        return null;
    }
}
