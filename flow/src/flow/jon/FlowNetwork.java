package flow.jon;

import util.jon.Pair;

public class FlowNetwork {
    //          Flow,    Capacity
    private Pair<Integer, Integer>[][] network;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int size) {
        network = (Pair<Integer, Integer>[][]) new Object[size][size];
    }

    public void addEdge(int from, int to, int capacity) {
        network[from][to] = Pair.of(0, capacity);
    }

    public Pair<Integer, Integer>[] getNode(int i) {
        return network[i];
    }

    public int getCapacity(int from, int to) {
        return network[from][to].right;
    }

    public void increaseFlow(int from, int to, int delta) {
        network[from][to].updateLeft(l -> l + delta);
    }

    public void decreaseFlow(int from, int to, int delta) {
        network[to][from].updateLeft(l -> l - delta);
    }
}
