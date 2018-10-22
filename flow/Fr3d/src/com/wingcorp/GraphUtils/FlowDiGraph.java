package com.wingcorp.GraphUtils;

import edu.princeton.cs.algs4.Bag;

public class FlowDiGraph {
    private final int V;
    private int E;
    private Bag<Edge>[] adj;

    public FlowDiGraph(int v) {
        V = v;
        E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new Bag();
        }
    }

    public int V(){
        return V;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    public void addEdge(int u, int v, int c) {
        validateVertex(u);
        validateVertex(v);

        var eu = new Edge(u, v, c);
        var ev = new Edge(v, u, c);

        adj[u].add(eu);
        adj[v].add(ev);

        E++;
        E++;
    }

    public Bag<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges \n");
        for (int v = 0; v < V; v++) {
            s.append(String.format("Adjacency list for %d: \n", v));
            for (Edge e : adj[v]) {
                s.append("\t" + e.toString() + "\n");
            }
            s.append("\n");
        }
        return s.toString();
    }
}
