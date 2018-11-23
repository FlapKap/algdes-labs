import edu.princeton.cs.algs4.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;


public class RedScare {

    public static void main(String[] args) throws FileNotFoundException {
        Map<String, InputStream> streams = new HashMap<>();

        //If we get an argument it is a path, either to a directory of files, or a single file
        if (args.length == 1) {
            var path = new File(args[0]);
            if (path.isDirectory()) {
                File[] files = path.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
                for (File file : files) { //Files can technically be null, but if it is, where screwed anyway
                    streams.put(file.getName(), new FileInputStream(file));
                }
            } else {
                streams.put(path.getName(), new FileInputStream(path));
            }

        } else {
            streams.put("Stdin", System.in);
        }

        streams.forEach((name, stream) -> {
            Graph graph = GraphParser.parse(stream);
            System.out.printf("Running on: %s\n", name);
            runProblemsAndPrint(graph);
        });

    }

    private static void runProblemsAndPrint(Graph graph) {
        //None
        Graph withoutReds = graph.copy((n) -> !n.isRed || (n.isSource || n.isSink));
        int withoutRedsSource = withoutReds.mapping.get(withoutReds.source);
        var withoutRedsPath = new BreadthFirstDirectedPaths(withoutReds.asDigraph(), withoutRedsSource);
        var withoutRedsSink = withoutReds.mapping.get(withoutReds.sink);

        int withoutRedsPathLength = (withoutRedsPath.hasPathTo(withoutRedsSink)) ? withoutRedsPath.distTo(withoutRedsSink) : -1;
        System.out.printf("None: %d\n", withoutRedsPathLength);

        // Some
        if (!graph.directed) {
            FlowNetwork someGraph = graph.asFlowNetwork(graph.V + 2, (from, to) -> 1);
            int sPrime = graph.V;
            int tPrime = graph.V + 1;
            // add new s and paths to old source and sink
            someGraph.addEdge(new FlowEdge(sPrime, graph.mapping.get(graph.source), 1));
            someGraph.addEdge(new FlowEdge(sPrime, graph.mapping.get(graph.sink), 1));

            // add red
            for (Node node : graph.nodes) {
                if (node.isRed) {
                    someGraph.addEdge(new FlowEdge(graph.mapping.get(node), tPrime, 2));
                }
            }
            FordFulkerson maxFlow = new FordFulkerson(someGraph, sPrime, tPrime);
            boolean someAnswer = ((int) maxFlow.value()) == 2;
            System.out.printf("Some: %s\n", someAnswer);
        } else {
            System.out.printf("Some: %s\n", false);
        }
        //TODO: somehow make sure that the path is simple -> that it does not hit the same vertex twice

        //Many
        if (graph.directed) {
            Graph same = graph.copy((n) -> true);
            int sameSink = same.mapping.get(same.source);
            int sameSource = same.mapping.get(same.sink);
            var edgeWeightedDiGraph = same.asEdgeWeightedDigraph((from, to) -> {
                if (from.isRed && to.isRed) {
                    return -2;
                }
                if (from.isRed) {
                    return -1;
                } else if (to.isRed) {
                    return -1;
                }
                return 1;
            });
            var bellmanFord = new BellmanFordSP(edgeWeightedDiGraph, sameSource);
            int numRedVertices = -1;
            try {
                if (bellmanFord.hasPathTo(sameSink)) {
                    var iterPath = bellmanFord.pathTo(sameSink);
                    numRedVertices = 0;
                    for (DirectedEdge s : iterPath) {
                        if (s.weight() < 0 && s.weight() > -1.5) numRedVertices += 1;
                        if (s.weight() < -1.5) numRedVertices += 2;
                    }
                }
            } catch (Exception e) {
                //TODO: handle exception
            } finally {
                System.out.printf("Many: %s\n", numRedVertices > 0 ? numRedVertices : -1);
            }
        } else {
            System.out.printf("Many: %s\n", -1);
        }

        //Few
        var few = graph.asEdgeWeightedDigraph((from, to) -> to.isRed ? 1 : 0);
        var fewSourceInt = graph.mapping.get(graph.source);
        var fewSinkInt = graph.mapping.get(graph.sink);
        var paths = new DijkstraSP(few, fewSourceInt);
        var fewCount = -1;
        if (paths.hasPathTo(fewSinkInt)) {
            fewCount = (int) Math.floor(paths.distTo(fewSinkInt));
            if (graph.source.isRed) fewCount++;
            if (graph.sink.isRed) fewCount++;
        }
        System.out.printf("Few: %d\n", fewCount);


        //Alternate
        Graph alternating = graph.copy((from, to) -> from.isRed ^ to.isRed); //XOR - both can't be the same
        boolean alternatingPathExists = false;
        if (alternating.mapping.containsKey(alternating.source) && alternating.mapping.containsKey(alternating.sink)) {
            var alternatingSource = alternating.mapping.get(alternating.source);
            var alternatingPath = new BreadthFirstDirectedPaths(alternating.asDigraph(), alternatingSource);
            var alternatingSink = alternating.mapping.get(alternating.sink);
            alternatingPathExists = alternatingPath.hasPathTo(alternatingSink);
        }
        System.out.printf("Alternate: %s\n", alternatingPathExists);
        System.out.println();
    }
}
