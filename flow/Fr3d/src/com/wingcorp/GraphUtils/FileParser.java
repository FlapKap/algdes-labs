package com.wingcorp.GraphUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileParser {
    public static FlowDiGraph Parse(String path) throws FileNotFoundException {
        Scanner s = new Scanner(new File(path));

        int V = Integer.parseInt(s.nextLine());
        var g = new FlowDiGraph(V);

        for (int i = 0; i < V; i++) {
            s.nextLine();
        }

        int E = Integer.parseInt(s.nextLine());

        for (int i = 0; i < E; i++) {
            var parts = s.nextLine().trim().split(" ");
            if (parts.length != 3) throw new IllegalArgumentException("Something is wrong about these edges (Does not consist of 3 parts)");

            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);
            if (c < 0) {
                c = Integer.MAX_VALUE;
            }

            g.addEdge(u, v, c);
        }

        return g;
    }
}
