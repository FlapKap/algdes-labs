package flow;

import util.jon.InputReader;
import util.jon.Pair;

import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NetworkParser {

    /**
     * Parses a network from an InputStream.
     *
     * @param inputStream the InputStream to parse.
     * @return A pair containing the labels from the original file and the parsed Network.
     */
    public static Pair<String[], Network> parseNetwork(InputStream inputStream) {
        InputReader ir = new InputReader(inputStream);

        var intPattern = Pattern.compile("^(\\d+)$");

        int vertices = Integer.parseInt(ir.findNext(intPattern).get(1));

        String[] labels = new String[vertices];
        for (int i = 0; i < vertices; i++) {
            labels[i] = ir.readLine().trim();
        }

        int edges = Integer.parseInt(ir.findNext(intPattern).get(1));

        var edgePattern = Pattern.compile("^(\\d+)\\s(\\d+)\\s(-?\\d+)$");

        var network = new Network(vertices);

        for (int i = 0; i < edges; i++) {
            var edge = ir.findNext(edgePattern).stream().skip(1).map(Integer::parseInt).collect(Collectors.toList());
            var weight = (edge.get(2) < 0) ? Integer.MAX_VALUE : edge.get(2);
            network.addEdge(edge.get(0), edge.get(1), weight);
            network.addEdge(edge.get(1), edge.get(0), weight);
        }

        return Pair.of(labels, network);
    }
}
