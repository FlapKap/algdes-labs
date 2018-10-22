package com.wingcorp.GraphUtils;

public class Edge {
    public final int U;
    public final int V;
    public final int Capacity;

    private int Flow = 0;
    private int FlowToU = 0;
    private int FlowToV = 0;

    public Edge(int u, int v, int c) {
        U = u;
        V = v;
        Capacity = c;
    }

    public int ResidualTo(int edge) {
        if (edge == FlowToU) return Capacity - FlowToU;
        return Capacity - FlowToV;
    }

    public int AddFlowTo(int edge, double flow) {
        if (edge == FlowToU) {
            FlowToU += flow;
            FlowToV -= flow;
        } else {
            FlowToV += flow;
            FlowToU -= flow;
        }

        Flow += flow;

        return Flow;
    }

    public int Flow() {
        return Flow;
    }

    public int other(int node) {
        if (node == U) return V;
        return U;
    }

    public String toString() {
        return U + "->" + V + " " + Flow + "/" + Capacity;
    }
}
