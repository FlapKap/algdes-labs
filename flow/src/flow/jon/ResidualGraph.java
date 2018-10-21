package flow.jon;


import util.jon.Pair;

public class ResidualGraph {
    public int[][] graph;

    public ResidualGraph(int size) {
        graph = new int[size][size];
    }

    public void addEdge(int from, int to, int capacity) {
        graph[from][to] = capacity;
    }

    public void increaseFlow(int from, int to, int delta) {
        graph[from][to] += delta;
    }

    public void decreaseFlow(int from, int to, int delta) {
        graph[to][from] -= delta;
    }

    public void resetFlow(int from, int to) {
        graph[from][to] = 0;
        graph[to][from] = 0;
    }

    public int getFlow(int from, int to) {
        return graph[from][to];
    }

    public Pair<Integer, Integer> getFlows(int from, int to) {
        return Pair.of(graph[from][to], graph[to][from]);
    }
}
