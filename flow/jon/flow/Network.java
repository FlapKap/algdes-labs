package flow;

public class Network {
    private final int[][] network;
    public final int source = 0;
    public final int sink;
    public final int size;

    @SuppressWarnings("unchecked")
    public Network(int size) {
        this.size = size;
        network = new int[size][size];
        sink = size - 1;
    }

    public void addEdge(int from, int to, int capacity) {
        network[from][to] = capacity;
    }

    public int getCapacity(int from, int to) {
        return network[from][to];
    }
}
