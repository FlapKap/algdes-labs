import edu.princeton.cs.algs4.*;

import java.util.*;
import java.util.function.*;


public class Graph {
    public final boolean directed;

    public final int V;
    public final int E;
    public final int R;
    public final Node source;
    public final Node sink;

    public final Map<Node, Integer> mapping = new HashMap<>();

    Set<Node> nodes;
    Map<Node, Set<Node>> edges;

    public Graph(Set<Node> nodes, Map<Node, Set<Node>> edges, Node source, Node sink, boolean directed, int V, int E, int R) {
        this.nodes = nodes;
        this.edges = edges;
        this.directed = directed;
        this.source = source;
        this.sink = sink;
        this.V = V;
        this.E = E;
        this.R = R;
        int i = 0;
        for (Node node : nodes) {
            mapping.put(node, i++);
        }

        if (this.source == null || this.sink == null) {
            // No path should exists if source nor sink is a part of the set
            throw new IllegalArgumentException("Source or sink are not connected with edges");
        }
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Graph copy(Predicate<Node> filter) {
        var filteredEdges = new HashMap<Node, Set<Node>>();
        edges.forEach((node, adj) -> {
            if (filter.test(node)) {
                var newAdj = new HashSet<Node>();
                filteredEdges.put(node, newAdj);
                adj.forEach(n -> {
                    if (filter.test(n)) newAdj.add(n);
                });
            }
        });
        var filteredNodes = new HashSet<>(nodes);
        nodes.forEach(n -> {
            if (filter.test(n)) filteredNodes.add(n);
        });


        return new Graph(filteredNodes, filteredEdges, source, sink, directed, V, E, R);
    }

    public Graph copy(BiPredicate<Node, Node> edgeFilter) {
        var filteredEdges = new HashMap<Node, Set<Node>>();
        var filteredNodes = new HashSet<Node>();

        edges.forEach((key, neighbours) -> {
            filteredEdges.putIfAbsent(key, new HashSet<>());
            for (Node neighbour : neighbours) {
                if (edgeFilter.test(key, neighbour)) {
                    filteredNodes.add(key);
                    filteredNodes.add(neighbour);
                    filteredEdges.get(key).add(neighbour);
                }
            }
        });

        return new Graph(filteredNodes, filteredEdges, source, sink, directed, V, E, R);
    }

    public Digraph asDigraph() {
        var graph = new Digraph(nodes.size());

        edges.forEach((key, value) -> {
            for (Node v : value) {
                graph.addEdge(mapping.get(key), mapping.get(v));
                if (!directed) {
                    graph.addEdge(mapping.get(v), mapping.get(key));
                }
            }
        });
        return graph;
    }

    public EdgeWeightedDigraph asEdgeWeightedDigraph(BiFunction<Node, Node, Integer> weightFunction) {
        var graph = new EdgeWeightedDigraph(nodes.size());

        edges.forEach((node, neighbours) -> {
            int from = mapping.get(node);
            for (Node n : neighbours) {
                int to = mapping.get(n);
                int weight = weightFunction.apply(node, n);
                graph.addEdge(new DirectedEdge(from, to, weight));
                if (!directed) {
                    graph.addEdge(new DirectedEdge(to, from, weight));
                }
            }
        });

        return graph;
    }

    public FlowNetwork asFlowNetwork(BiFunction<Node, Node, Integer> capacityFunction) {
        return asFlowNetwork(null, capacityFunction);
    }

    public FlowNetwork asFlowNetwork(Integer newSize, BiFunction<Node, Node, Integer> capacityFunction) {
        //if (directed) throw new UnsupportedOperationException("Frick off. This only works for undirected graphs");
        //after pondering a bit, i think the above is a mistake -khjo

        var flowGraph = (newSize == null) ? new FlowNetwork(V) : new FlowNetwork(newSize);

        edges.forEach((node, neighbours) -> {
            int from = mapping.get(node);
            for (Node n : neighbours) {
                int to = mapping.get(n);
                int capacity = capacityFunction.apply(node, n);
                flowGraph.addEdge(new FlowEdge(from, to, capacity));
                if (!directed) {
                    flowGraph.addEdge(new FlowEdge(to, from, capacity));
                }
            }
        });

        return flowGraph;
    }

    @Override
    public String toString() {
        var output = new StringBuilder();
        edges.forEach(((n, adj) -> {
            for (Node m : adj) {
                output.append(String.format("%s -> %s\n", n.label, m.label));
            }
        }));
        return output.toString();
    }
}

/*
1. Parse file
    -> Get data on size
    -> node info (red, and crap)
    -> edges (string -> string)

2. Filter nodes based on task and return a resulting graph
    - Hmmm...
    - Filter nodes based on red property
    - Construct DiGraph with edges that fit the filter
*/