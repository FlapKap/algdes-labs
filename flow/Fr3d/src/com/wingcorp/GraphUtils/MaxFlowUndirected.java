package com.wingcorp.GraphUtils;

import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Comparator;

public class MaxFlowUndirected {
    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
    private double value;         // current value of max flow

    public MaxFlowUndirected(FlowDiGraph G, int s, int t) {

        // while there exists an augmenting path, use it
        while (hasAugmentingPath(G, s, t)) {

            // compute bottleneck capacity
            double bottle = bottleneck(s, t);

            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].AddFlowTo(v, bottle);
            }

            // Increase the value by the bottleneck
            value += bottle;
        }

        // Print the minimum cut
        for(String cut : findMinCuts(G)) {
            System.out.println(cut);
        }
    }

    public void printValue() {
        System.out.println("Value: " + value);
    }

    // Checks if there is a path to be augmented from s to t in the graph using breadth-first-search
    private boolean hasAugmentingPath(FlowDiGraph G, int s, int t) {
        edgeTo = new Edge[G.V()];
        marked = new boolean[G.V()];

        // breadth-first search
        Queue<Integer> queue = new Queue<>();
        queue.enqueue(s);
        marked[s] = true;

        // While the queue is not empty and terminus has not been marked by the search
        while (!queue.isEmpty() && !marked[t]) {

            // Grab an element
            int v = queue.dequeue();

            // For each edge adjacent to it
            for (Edge e : G.adj(v)) {

                // Get the vertex on the other side of this edge that is not the current one
                int w = e.other(v);

                // If residual capacity from v to w is greater than 0
                if (e.ResidualTo(w) > 0) {

                    // And has not been marked
                    if (!marked[w]) {

                        // Mark it and enqueue the next vertex
                        edgeTo[w] = e;
                        marked[w] = true;
                        queue.enqueue(w);
                    }
                }
            }
        }

        return marked[t];
    }

    // Computes the bottleneck of the path between s and t
    private double bottleneck(int s, int t) {
        double bottle = Double.POSITIVE_INFINITY;
        for (int v = t; v != s; v = edgeTo[v].other(v)) {
            bottle = Math.min(bottle, edgeTo[v].ResidualTo(v));
        }
        return bottle;
    }

    // Finds the minimum cuts in the graph and returns the (u, v, c) pairs in lexicographical order
    private ArrayList<String> findMinCuts(FlowDiGraph G) {
        var minCuts = new ArrayList<String>();
        for (int v = 0; v < G.V(); v++) {
            if (marked[v]) {
                var adj = G.adj(v);
                for(Edge e : adj){
                    // Get an adjacent vertex and check if it is in the same side of the cut
                    if (!marked[e.other(v)]){
                        // If not, we have a min-cut edge
                        minCuts.add(Math.min(e.other(v), v) + " " + Math.max(e.other(v), v) + " " + e.Flow());
                    }
                }
            }
        }
        minCuts.sort(Comparator.naturalOrder());
        return minCuts;
    }
}
