package rs

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths

object RedScare {

    private fun copy(
            g: Graph,
            vertexFilter: ((Node) -> Boolean)? = null,
            edgeFilter: ((Edge) -> Boolean)? = null,
            vertexMapper: ((Node) -> Node)? = null,
            edgeMapper: ((Edge) -> Edge)? = null
    ): Graph {
        val someGraph = DirectedSparseMultigraph<Node, Edge>()
        var vertices = g.graph.vertices
        if (vertexFilter != null) {
            vertices = vertices.filter(vertexFilter)
        }
        if (vertexMapper != null) {
            vertices = vertices.map(vertexMapper)
        }
        vertices.forEach {
            someGraph.addVertex(it)
        }
        var edges = g.graph.edges
        if (edgeFilter != null) {
            edges = edges.filter(edgeFilter)
        }
        if (edgeMapper != null) {
            edges = edges.map(edgeMapper)
        }
        edges.forEach {
            someGraph.addEdge(it, g.graph.getSource(it), g.graph.getDest(it))
        }
        return Graph(someGraph, g.undirected, g.source, g.sink)
    }

    private val buildPathString = { acc: StringBuilder, edge: Edge ->
        acc.append("\t  ${edge.label}\n")
        acc
    }

    private fun findPath(g: Graph): String {
        return DijkstraShortestPath(g.graph)
                .getPath(g.source, g.sink)
                .fold(StringBuilder(), buildPathString)
                .removeSuffix("\n").toString()
    }

    private fun findMaxFlow(g: Graph, edgeCapTransformer: (Edge) -> Number): String {
        val maxFlow = EdmondsKarpMaxFlow(
                g.graph,
                g.source,
                g.sink,
                { e: Edge? -> edgeCapTransformer(e!!) },
                mutableMapOf<Edge, Number>(),
                { Edge() }
        )
//        return maxFlow.minCutEdges
//                .fold(StringBuilder()) { acc, edge ->
//                    acc.append("\t  ${edge.label}\n")
//                    return@fold acc
//                }.removeSuffix("\n").toString()
        maxFlow.evaluate()
        return DijkstraShortestPath(maxFlow.flowGraph)
                .getPath(g.source, g.sink)
                .fold(StringBuilder(), buildPathString)
                .removeSuffix("\n").toString()
    }

    private fun answer(path: String): String {
        return if (path.isBlank()) "\tNo." else "\tYes:\n$path"
    }

    /**
     * This is the interesting function that takes care of business.
     */
    private fun solveProblems(file: String, g: Graph): String {

        val noReds = copy(g, edgeFilter = { !it.adjacentToRed })
        val noRedsPath = findPath(noReds)

        val onlyReds = copy(g, edgeFilter = { it.adjacentToRed })
        val onlyRedsPath = findPath(onlyReds)

        val many = copy(g, edgeFilter = { !it.adjacentToRed })
        val manyPath =
                if (g.undirected)
                    findMaxFlow(many) { 1 }
                else ""

        val few = copy(g)
        val fewPath = findMaxFlow(few) { 1 }

        val alternating = copy(g, edgeFilter = {
            it.from.isRed xor it.to.isRed
        })
        val alternatingPath = findPath(alternating)

        return """
        ### $file
          None:
        ${answer(noRedsPath)}

          Some:
        ${answer(onlyRedsPath)}

          Many:
        ${answer(manyPath)}

          Few:
        ${answer(fewPath)}

          Alternating:
        ${answer(alternatingPath)}
        ###
        """
                .replace("    ", "")
    }

    private fun loadFiles(dataPath: String): List<Pair<String, FileInputStream>> {
        return File(dataPath).walk(FileWalkDirection.TOP_DOWN)
                .filter { it.name.endsWith(".txt") }
                .map { f: File -> Pair(f.name!!, f.inputStream()) }
                .toList()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("This program requires a path to a data directory or a data file to run.")
            return
        }
        if (args.size == 2) {
            val path = Paths.get(args[1])
            if (!Files.exists(path)) {
                File(args[1]).parentFile.mkdirs()
            }
            System.setOut(PrintStream(FileOutputStream(args[1])))
        }
        val dataPath = args.first()
        if (dataPath.endsWith(".txt")) {
            val dataStream = FileInputStream(dataPath)
            val graph = GraphParser.parse(dataStream)
            println(solveProblems(dataPath, graph))
        } else {
            val dataFiles = loadFiles(dataPath)
            dataFiles
                    .parallelStream()
                    .map { p -> Pair(p.first, GraphParser.parse(p.second)) }
                    .map { p -> Pair(p.first, solveProblems(p.first, p.second)) }
                    .sorted(compareBy { it.first })
                    .forEachOrdered { println(it.second) }
        }
    }
}