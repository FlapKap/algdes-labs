package flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import util.jon.Triplet;
import util.jon.UTriplet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static util.jon.Utils.consecutive;

class MinimumCutterTest {

    /*
             B -----10---> C -----+
             ^                    |
             |                    |
             10                   5
             |                    |
        A +--+                    +--> E
             |                    |
             +---8--> D -----8----+
     */

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
                .stream().sorted())
                .map(p -> Triplet.of(p.left, p.right, n.getCapacity(p.left, p.right)))
                .collect(Collectors.toList());

        final var minCutter = new MinimumCutter(n);

        final var actualResult = minCutter.minimumCut();
        assertEquals(expectedMaxFlow, actualResult.left.intValue());
        assertIterableEquals(expectedMinimumCut, actualResult.right);
    }

    @Test
    void slideTest() {
        final var n = new Network(6);
        final var s = n.source;
        final var t = n.sink;
        n.addEdge(s, 1, 10);
        n.addEdge(s, 2, 10);
        n.addEdge(1, 2, 2);
        n.addEdge(1, 3, 4);
        n.addEdge(1, 4, 8);
        n.addEdge(2, 4, 9);
        n.addEdge(4, 3, 6);
        n.addEdge(3, t, 10);
        n.addEdge(4, t, 10);

        final var maxFlow = 19;

        final var minCut = Set.of(UTriplet.from(0, 1, 10), UTriplet.from(2, 4, 9));

        final var minCutter = new MinimumCutter(n);

        final var actual = minCutter.minimumCut();

        assertEquals(maxFlow, actual.left.intValue());
        assertIterableEquals(minCut, actual.right);
    }
}