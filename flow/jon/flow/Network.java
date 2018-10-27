package flow;

public class Network {
    private final int[][] network;
    public final int source = 0;
    public final int sink;
    public final int size;
    private int edges = 0;

    @SuppressWarnings("unchecked")
    public Network(int size) {
        this.size = size;
        network = new int[size][size];
        sink = size - 1;
    }

    public int getEdges() {
        return edges;
    }

    public void addEdge(int from, int to, int capacity) {
        network[from][to] = capacity;
        edges++;
    }

    public int getCapacity(int from, int to) {
        return network[from][to];
    }
}
