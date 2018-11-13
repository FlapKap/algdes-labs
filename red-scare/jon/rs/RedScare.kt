package rs

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths

object RedScare {

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

    private fun answer(path: String): String {
        return if (path.isBlank()) "\tNo." else "\tYes:\n$path"
    }

    /**
     * This is the interesting function that takes care of business.
     */
    private fun solveProblems(file: String, g: Graph): String {

        val noReds = g.copy(edgeFilter = { !it.adjacentToRed })
        val noRedsPath = findPath(noReds)

        val someReds = g.copy(edgeFilter = { it.adjacentToRed })
        val someRedsPath = findPath(someReds)

        val many = g.copy(edgeFilter = { !it.adjacentToRed })
        val manyPath =
                if (g.undirected) {
                    val mf = g.maxFlow(many) { 1 }
                    findPath(mf.flowGraph())
                } else ""

        val few = g.copy()
        val fewPath =
                if (g.undirected) {
                    val mf = g.maxFlow(few) { 1 }
                    findPath(mf.flowGraph())
                } else ""

        val alternating = g.copy(edgeFilter = {
            it.from.isRed xor it.to.isRed
        })
        val alternatingPath = findPath(alternating)

        return """
        ### $file
          None:
        ${answer(noRedsPath)}

          Some:
        ${answer(someRedsPath)}

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