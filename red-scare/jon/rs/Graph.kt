package rs

import edu.uci.ics.jung.graph.DirectedGraph
import edu.uci.ics.jung.graph.DirectedSparseMultigraph

class Graph(
        val graph: DirectedGraph<Node, Edge>,
        val undirected: Boolean,
        val source: Node,
        val sink: Node
) {
    fun copy(
            vertexFilter: ((Node) -> Boolean)? = null,
            edgeFilter: ((Edge) -> Boolean)? = null,
            vertexMapper: ((Node) -> Node)? = null,
            edgeMapper: ((Edge) -> Edge)? = null
    ): Graph {
        val someGraph = DirectedSparseMultigraph<Node, Edge>()
        var vertices = graph.vertices
        if (vertexFilter != null) {
            vertices = vertices.filter(vertexFilter)
        }
        if (vertexMapper != null) {
            vertices = vertices.map(vertexMapper)
        }
        vertices.forEach {
            someGraph.addVertex(it)
        }
        var edges = graph.edges
        if (edgeFilter != null) {
            edges = edges.filter(edgeFilter)
        }
        if (edgeMapper != null) {
            edges = edges.map(edgeMapper)
        }
        edges.forEach {
            someGraph.addEdge(it, graph.getSource(it), graph.getDest(it))
        }
        return Graph(someGraph, undirected, source, sink)
    }

    fun maxFlow(g: Graph, edgeCapTransformer: (Edge) -> Number): MaxFlow {
        return MaxFlow(g, edgeCapTransformer)
    }
}