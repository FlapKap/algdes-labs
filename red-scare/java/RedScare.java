import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

import java.io.*;


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

        
    }
}
