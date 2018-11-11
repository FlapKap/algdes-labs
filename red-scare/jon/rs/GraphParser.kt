package rs

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import util.jon.InputReader
import java.io.InputStream
import java.util.regex.Pattern

/**
 * A module for parsing the graphs
 */
object GraphParser {

    fun parse(inputStream: InputStream): DirectedSparseMultigraph<Node, Edge> {
        val reader = InputReader(inputStream)
        val headerPattern = Pattern.compile("(\\d+) (\\d+) (\\d+)")

        val header = reader.findNext(headerPattern).drop(1).map { s: String -> Integer.parseInt(s) }
        val v = header[0]
        val e = header[1]

        val g = DirectedSparseMultigraph<Node, Edge>()

        val sTPattern = Pattern.compile("(\\w+) (\\w+)")

        val sT = reader.findNext(sTPattern)
        val sourceLabel = sT[1].trim()
        val sinkLabel = sT[2].trim()

        val nodes = mutableMapOf<String, Node>()

        val wsPat = Pattern.compile("\\s")

        for (i in 0 until v) {
            val line = reader.readLine().trim()
            val red = line.endsWith("*")
            val label = line.split(wsPat).first().trim()
            nodes[label] = Node(red, label, label.contentEquals(sourceLabel), label.contentEquals(sinkLabel))
        }

        nodes.values.forEach { g.addVertex(it) }

        for (i in 0 until e) {
            val line = reader.readLine().trim()
            val segments = line.split(wsPat).map { it.trim() }
            val fromLabel = segments.first()
            val toLabel = segments.last()
            val undirected = (segments[1].contentEquals("--"))
            val from = nodes[fromLabel]
            val to = nodes[toLabel]
            val adjacentToRed = from!!.isRed || to!!.isRed
            val label = "$fromLabel ${if (undirected) "--" else "->" } $toLabel"
            g.addEdge(Edge(label, adjacentToRed), from, to)
            if (undirected) {
                val revLabel = "$toLabel -- $fromLabel"
                g.addEdge(Edge(revLabel, adjacentToRed), to, from)
            }
        }

        return g
    }

}