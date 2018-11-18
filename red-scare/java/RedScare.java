import edu.princeton.cs.algs4.BellmanFordSP;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.DirectedEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RedScare {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = System.in;
        switch (args.length) {
            case 1: 
                stream = new FileInputStream(new File(args[0]));
                break;
            case 2:
                //TODO: @Jon you did something funky here and i dont know why
                //Theory: You allow one to input a filepath and the program should then write to that file
                //You are right on the money. //Jon
                break;
        }

        Graph graph = GraphParser.parse(stream);

        //None
        Graph withoutReds = graph.copy((n) -> !n.isRed || (n.isSource || n.isSink));
        var withoutRedsSource = withoutReds.mapping.get(withoutReds.source);
        var withoutRedsPath = new BreadthFirstDirectedPaths(withoutReds.asDigraph(), withoutRedsSource);
        var withoutRedsSink = withoutReds.mapping.get(withoutReds.sink);

        int withoutRedsPathLength = (withoutRedsPath.hasPathTo(withoutRedsSink)) ? withoutRedsPath.distTo(withoutRedsSink) : -1;
        System.out.printf("None: %d\n", withoutRedsPathLength);

        //Many
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
        try {
            var iterPath = bellmanFord.pathTo(sameSink);
            int numRedVertices = 0;
            for (DirectedEdge s : iterPath) {
                if (s.weight() < 0 && s.weight() > -1.5) numRedVertices += 1;
                if (s.weight() < -1.5) numRedVertices += 2;
            }
            System.out.printf("Many: %s\n", numRedVertices > 0 ? numRedVertices : -1);
        } catch (UnsupportedOperationException e) {
            System.out.printf("Many: %s\n", -1);
            //TODO: handle exception
        }

        //Alternate
        Graph alternating = graph.copy((from, to) -> from.isRed ^ to.isRed); //XOR - both can't be the same
        var alternatingSource = alternating.mapping.get(alternating.source);
        var alternatingPath = new BreadthFirstDirectedPaths(alternating.asDigraph(), alternatingSource);
        var alternatingSink = alternating.mapping.get(alternating.sink);

        boolean alternatingPathExists = alternatingPath.hasPathTo(alternatingSink);
        System.out.printf("Alternate: %s\n", alternatingPathExists);
    }
}
