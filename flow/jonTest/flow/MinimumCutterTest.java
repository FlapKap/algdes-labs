package flow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import util.jon.Triplet;
import util.jon.Utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static util.jon.Utils.consecutive;

class MinimumCutterTest {

    @Test
    void minimumCut() {
        final var n = new Network(5);
        final var source = n.source;
        final var sink = n.sink;
        n.addEdge(source, 1, 10);
        n.addEdge(1, 2, 10);
        n.addEdge(2, sink, 5);
        n.addEdge(source, 3, 8);
        n.addEdge(3, sink, 8);

        final int expectedMaxFlow = 5 + 8;

        final List<Triplet<Integer, Integer, Integer>> expectedMinimumCut = consecutive(Set.of(0, 1, 2)
                .stream())
                .map(p -> Triplet.of(p.left, p.right, n.getCapacity(p.left, p.right)))
                .collect(Collectors.toList());

        final var minCutter = new MinimumCutter(n);

        final var actualResult = minCutter.minimumCut();
        assertEquals(expectedMaxFlow, actualResult.left.intValue());
        assertIterableEquals(expectedMinimumCut, actualResult.right);
    }
}