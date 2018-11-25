import edu.princeton.cs.algs4.*;

import java.io.*;
import java.util.*;
import java.util.stream.StreamSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


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
        Map<String, Map<Problem, String>> results = new TreeMap<>();
        Map<String, Graph> graphs = new HashMap<>();
        streams.forEach((name, stream) -> {
            Graph graph = GraphParser.parse(stream);
            graphs.put(name, graph);
            System.out.printf("Running on: %s\n", name);
            results.put(name, runProblemsAndPrint(graph));
        });

        //print latex table
        System.out.println("AWESOME LATEX TABLE INCOMING BITCHES 👌👌👌");
        System.out.println("-------------------------------------------------");
        var sb = new StringBuilder();
        sb.append("\\begin{longtable}{lrrrrrr}\n" +
                "  \\toprule\n" +
                "  Instance name & $n$ & A & F & M & N & S \\\\\n" +
                "  \\midrule\n");

        results.forEach((name, result) -> {
            if (graphs.get(name).V >= 0) {
                sb.append(String.format(
                        "    %s & %d & %s & %s & %s & %s & %s \\\\ \n",
                        name,
                        graphs.get(name).V,
                        result.get(Problem.Alternate),
                        result.get(Problem.Few),
                        result.get(Problem.Many),
                        result.get(Problem.None),
                        result.get(Problem.Some)
                        )
                );
            }
        });
        sb.append("  \\bottomrule\n\\end{longtable}");
        System.out.println(sb);
        System.out.println("-------------------------------------------------");
        System.out.println("HOLY FUCK THAT TABLE IS AMAZING 😲 💻 💼💼💼");


        System.out.println("IT WAS SO AWESOME WE HAVE TO DO IT AGAIN (to make the result.csv file)");
        System.out.println("-------------------------------------------------");
        var sb2 = new StringBuilder();
        sb2.append("name\tn\tA\tF\tM\tN\tS\n");

        results.forEach((name, result) -> sb2.append(String.format(
                "%s\t%d\t%s\t%s\t%s\t%s\t%s\n",
                name,
                graphs.get(name).V,
                result.get(Problem.Alternate),
                result.get(Problem.Few),
                result.get(Problem.Many),
                result.get(Problem.None),
                result.get(Problem.Some)
                )
                )
        );
        System.out.println(sb2);
        System.out.println("-------------------------------------------------");
        System.out.println("JESUS CHRIST I LOVE IT");

    }

    enum Problem {
        None,
        Some,
        Many,
        Few,
        Alternate
    }

    private static Map<Problem, String> runProblemsAndPrint(Graph graph) {
        Map<Problem, String> result = new HashMap<>();
        //None

        Graph withoutReds = graph.copy((n) -> !n.isRed || (n.isSource || n.isSink));
        int withoutRedsSource = withoutReds.mapping.get(withoutReds.source);
        var withoutRedsPath = new BreadthFirstDirectedPaths(withoutReds.asDigraph(), withoutRedsSource);
        var withoutRedsSink = withoutReds.mapping.get(withoutReds.sink);

        int withoutRedsPathLength = (withoutRedsPath.hasPathTo(withoutRedsSink)) ? withoutRedsPath.distTo(withoutRedsSink) : -1;
        System.out.printf("None: %d\n", withoutRedsPathLength);
        result.put(Problem.None, Integer.toString(withoutRedsPathLength));


        // Some
        boolean someAnswer = false;
        for (Node node : graph.nodes) {
            if (node.isRed) {
                FlowNetwork someGraph = graph.asFlowNetwork(graph.V + 2, (from, to) -> 1);
                int sPrime = graph.V;
                int tPrime = graph.V + 1;
                // add new s and paths to old source and sink
                someGraph.addEdge(new FlowEdge(sPrime, graph.mapping.get(graph.source), 1));
                someGraph.addEdge(new FlowEdge(sPrime, graph.mapping.get(graph.sink), 1));

                someGraph.addEdge(new FlowEdge(graph.mapping.get(node), tPrime, 2));

                FordFulkerson maxFlow = new FordFulkerson(someGraph, sPrime, tPrime);

                someAnswer = ((int) maxFlow.value()) >= 2;
                if (someAnswer) break;
            }
        }
        System.out.printf("Some: %s\n", someAnswer);
        result.put(Problem.Some, Boolean.toString(someAnswer));

        //TODO: somehow make sure that the path is simple -> that it does not hit the same vertex twice

        //Many
        Graph same = graph.copy((n) -> true);
        var lookup = new TreeMap<Integer, Node>();
        same.mapping.forEach((n, i) -> lookup.put(i, n));
        int sameSource = same.mapping.get(same.source);
        int sameSink = same.mapping.get(same.sink);
        var edgeWeightedDiGraph = same.asEdgeWeightedDigraph((from, to) -> {
            if (from.isRed && to.isRed) {
                return -2;
            }
            if (from.isRed) {
                return -1;
            } else if (to.isRed) {
                return -1;
            }
            return 0;
        });
        var bellmanFord = new BellmanFordSP(edgeWeightedDiGraph, sameSource);

        if (!same.directed) {
            //not a Directed: not a DAG
            System.out.println("Many: ?!");
            result.put(Problem.Many, "?!");
        } else if (!bellmanFord.hasPathTo(sameSink)) {
            //No path
            System.out.println("Many: -1");
            result.put(Problem.Many, "-1");
        } else {
            try {
                var iterPath = bellmanFord.pathTo(sameSink);
                var numRedVertices = StreamSupport.stream(iterPath.spliterator(), false)
                        .map(e -> ((lookup.get(e.from()).isRed) ? 1 : 0) + ((lookup.get(e.from()).isRed) ? 1 : 0))
                        .mapToInt(e -> e)
                        .sum();
                System.out.printf("Many: %s\n", numRedVertices);
                result.put(Problem.Many, Integer.toString(numRedVertices));
            } catch (UnsupportedOperationException e) {
                //There is a cycle: not a DAG
                System.out.println("Many: ?!");
                result.put(Problem.Many, "?!");
            }
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
        result.put(Problem.Few, Integer.toString(fewCount));

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
        result.put(Problem.Alternate, Boolean.toString(alternatingPathExists));

        return result;
    }
}
