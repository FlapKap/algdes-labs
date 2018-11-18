import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;


import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;


public class Graph {
    public final boolean directed;

    public final Node source;
    public final Node sink;

    public final Map<Node, Integer> mapping = new HashMap<>();

    Set<Node> nodes;
    Map<Node, Set<Node>> edges;

    public Graph(Set<Node> nodes, Map<Node, Set<Node>> edges, Node source, Node sink, boolean directed) {
        this.nodes = nodes;
        this.edges = edges;
        this.directed = directed;
        this.source = source;
        this.sink = sink;
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
        var filteredNodes = nodes.stream()
                .filter(filter)
                .collect(Collectors.toSet());

        // var filteredNodes = new ArrayList<Node>(nodes);
        // filteredNodes.removeIf(filter.negate());

        Map<Node, Set<Node>> filteredEdges = new HashMap<>(edges);
        filteredEdges.keySet().removeIf(filter.negate());
        filteredEdges.values().forEach(s -> s.removeIf(filter.negate()));

        return new Graph(filteredNodes, filteredEdges, source, sink, directed);
    }

    public Graph copy(BiPredicate<Node, Node> edgeFilter) {
        var filteredEdges = new HashMap<Node, Set<Node>>();
        var filteredNodes = new HashSet<Node>();
        
        edges.forEach((key, neighbours) -> {
            filteredEdges.putIfAbsent(key, new HashSet<>());
            for (Node neighbour : neighbours) {
                if(edgeFilter.test(key, neighbour)) {
                    filteredNodes.add(key);
                    filteredNodes.add(neighbour);                    
                    filteredEdges.get(key).add(neighbour);                    
                }
            }
        });
        
        return new Graph(filteredNodes, filteredEdges, source, sink, directed);
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
        
        edges.forEach((key, value) -> {
            for (Node v: value) {
                int from = mapping.get(key);
                int to = mapping.get(v);
                int weight = weightFunction.apply(key, v);
                graph.addEdge(new DirectedEdge(from, to, weight));
                if(!directed) {
                    graph.addEdge(new DirectedEdge(to, from, weight));
                }
            }
        });

        return graph;
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