package rs

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger

object RedScare {

    private val buildPathString = { acc: StringBuilder, edge: Edge ->
        acc.append("\t  ${edge.label}\n")
        acc
    }

    private fun shortestPath(g: Graph, wf: ((Edge) -> Number)? = null): List<Edge> {
        val d = if (wf != null)
            DijkstraShortestPath(g.graph) { wf(it!!) }
        else
            DijkstraShortestPath(g.graph)
        return d.getPath(g.source, g.sink)
    }

    private fun pathString(edges: List<Edge>): String {
        return edges.fold(StringBuilder(), buildPathString)
                .removeSuffix("\n").toString()
    }

    private fun answer(path: String): String {
        return if (path.isBlank()) "\tNo." else "\tYes:\n$path"
    }

    /**
     * This is the interesting function that takes care of business.
     */
    private fun solveProblems(file: String, g: Graph): String {

        val noReds = g.copy(edgeFilter = { !it.adjacentToRed })
        val noRedsPath = shortestPath(noReds)

//        val someReds = g.copy(edgeFilter = { it.adjacentToRed })
        val someRedsPath = listOf<Edge>()//shortestPath(someReds)

        val many = g.copy()
        fun incidentHeuristic (e: Edge): Int {
            return g.graph.getIncidentEdges(e.to)
                    .fold(0) { acc, edge ->
                        if (edge.adjacentToRed) {
                            acc - 1
                        } else acc + 1
                    }
        }
        fun peakAheadHeuristic (e: Edge): Int {
            return g.graph.getIncidentEdges(e.to)
                    .filter { it != e }
                    .map { incidentHeuristic(it) }
                    .sum()
        }
        val manyPath = shortestPath(many) { e ->
            (Int.MAX_VALUE / 2) + incidentHeuristic(e) + peakAheadHeuristic(e)
        }

        var few = g.copy()
        var fewPath: List<Edge>
        do {
            fewPath = shortestPath(g)
            if (!fewPath.any { it.adjacentToRed }) {
                val pathSet = fewPath.toSet()
                few = few.copy(edgeFilter = { !pathSet.contains(it) && it.adjacentToRed })
            } else break
        } while (shortestPath(few).isNotEmpty())
        if (fewPath.all { !it.adjacentToRed }) {
            fewPath = listOf()
        }

        val alternating = g.copy(edgeFilter = {
            it.from.isRed xor it.to.isRed
        })
        val alternatingPath = shortestPath(alternating)

        return """
        ### $file
          None:
        ${answer(pathString(noRedsPath))}

          Some:
        ${answer(pathString(someRedsPath))}

          Many:
        ${answer(pathString(manyPath))}

          Few:
        ${answer(pathString(fewPath))}

          Alternating:
        ${answer(pathString(alternatingPath))}
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
            val counter = AtomicInteger(0)
            dataFiles
                    .stream()
                    .parallel()
                    .map { p -> Pair(p.first, GraphParser.parse(p.second)) }
                    .map { p -> Pair(p.first, solveProblems(p.first, p.second)) }
                    .forEach {
                        println(it.second)
                        println("Completed problem ${counter.incrementAndGet()} / ${dataFiles.size}")
                    }
        }
    }
}