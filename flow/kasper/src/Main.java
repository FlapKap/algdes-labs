import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static Edge[] parse(InputStream file) {
        Scanner sc = new Scanner(file);

        int numNodes = sc.nextInt();
        Node[] nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new Node(i, sc.next());
        }

        int numEdges = sc.nextInt();
        Edge[] edges = new Edge[numEdges];
        for (int i = 0; i < numEdges; i++) {
            edges[i] = new Edge(
                    nodes[sc.nextInt()],
                    nodes[sc.nextInt()],
                    sc.nextInt());
        }

        return edges;
    }

    private static Edge[] toDirectGraph(Edge[] edges){
        Edge[] output = new Edge[edges.length*2];

        for (int i = 0; i < output.length; i+=2) {
            Edge edge = edges[i / 2];
            output[i] = edge;
            output[i+1] = new Edge(edge.b, edge.a, edge.capacity);
        }
        return output;
    }

    public static void main(String[] args) throws IOException {

        Edge[] edges = null;

        switch (args.length) {
            case 0:
                System.out.println("You need to provide a filename or a file through stdin");
                System.exit(-1);
                break;
            case 1:
                try (InputStream file = new FileInputStream(args[0])) {
                    edges = parse(file);
                }
                break;
            default:
                edges = parse(System.in);
                break;
        }
        edges = toDirectGraph(edges);
        System.out.println(Arrays.toString(edges));
    }

}

class Node {
    int id;
    String name;

    Node(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Node{id=%d, name='%s'}", id, name);
    }
}

class Edge {
    Node a;
    Node b;
    int capacity;

    Edge(Node a, Node b, int capacity) {
        this.a = a;
        this.b = b;
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("Edge{a=%s, b=%s, capacity=%d}", a, b, capacity);
    }
}
