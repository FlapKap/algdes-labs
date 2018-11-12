package rs

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.io.FileInputStream

typealias Graph = DirectedSparseMultigraph<Node, Edge>

object RedScare {

    private fun copy(g: Graph, vertexFilter: ((Node) -> Boolean)? = null,
                     edgeFilter: ((Edge) -> Boolean)? = null): Graph {
        val someGraph = Graph()
        var vertices = g.vertices
        if (vertexFilter != null) {
            vertices = vertices.filter(vertexFilter)
        }
        vertices.forEach {
            someGraph.addVertex(it)
        }
        var edges = g.edges
        if (edgeFilter != null) {
            edges = edges.filter(edgeFilter)
        }
        edges.forEach {
            someGraph.addEdge(it, g.getSource(it), g.getDest(it))
        }
        return someGraph
    }

    private fun nonePath(g: Graph) {
        val noneGraph = copy(g, edgeFilter = { !it.adjacentToRed })
        val path = DijkstraShortestPath(noneGraph)
        path
                .getPath(noneGraph.vertices.first { it.isSource }, noneGraph.vertices.first { it.isSink })
                .forEach {
                    println(it.label)
                }
    }

    private fun somePath(g: Graph) {
        val someGraph = copy(g)
        val source = someGraph.vertices.first { it.isSource }
        val dest = someGraph.vertices.first { it.isSink }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("This program requires a data file as an argument.")
            return
        }
        val file = args.first()
        val g = GraphParser.parse(FileInputStream(file))
        nonePath(g)
    }
}