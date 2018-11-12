import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import util.jon.InputReader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphParser {

    private GraphParser() {
    }

    public static EdgeWeightedDigraph parse(InputStream stream) {
        var reader = new InputReader(stream);
        var headerPattern = Pattern.compile("(\\d+) (\\d+) (\\d+)");

        var header = reader.findNext(headerPattern).stream()
                .skip(1).map(Integer::parseInt).collect(Collectors.toList());
        var v = header.get(0);
        var e = header.get(1);

        var g = new EdgeWeightedDigraph(v, e);

        var sTPattern = Pattern.compile("(\\w+) (\\w+)");

        var sT = reader.findNext(sTPattern);
        var sourceLabel = sT.get(1).trim();
        var sinkLabel = sT.get(2).trim();

        var nodes = new String[v];

        IntStream.range(0, v).forEach(i -> {
            var line = reader.readLine().trim();
            var red = line.endsWith("*");
            var label = Arrays.stream(line.split("\\s")).map(String::trim)
                    .findFirst().get();
            nodes[i] = label;
        });

        throw new RuntimeException("NOT IMPLEMENTED");
    }
}
