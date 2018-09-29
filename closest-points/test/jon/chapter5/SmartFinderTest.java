package jon.chapter5;

import jon.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static jon.chapter5.TestHelper.randomPoints;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartFinderTest {

    private final SmartFinder testFinder = new SmartFinder();

    /*
     *   +----------------------------+
     * 10| a                          |
     *  9|                         j  |
     *  8|  b                         |
     *  7|             f       i      |
     *  6|                            |
     *  5|                            |
     *  4|        e           h       |
     *  3|                            |
     *  2|                            |
     *  1|                   g        |
     *  0|  cd                        |
     *   +----------------------------+
     *    0123456789012345678901234567
     *              1         2
     */
    @Test
    void closestPairFinderTest() {
        var points = new Point[]{
                new Point("a", 1., 10.),
                new Point("b", 2., 8.),
                new Point("c", 2., 0.),
                new Point("d", 3., 0.),
                new Point("e", 8., 4.),
                new Point("f", 13., 7.),
                new Point("g", 19., 1.),
                new Point("h", 20., 4.),
                new Point("i", 21., 7.),
                new Point("j", 26., 9.)
        };

        var expected = new Pair<>(points[2], points[3]);
        var expectedLeft = expected.left;
        var expectedRight = expected.right;

        var pointsList = Arrays.asList(points);

        var actual = testFinder.findClosestPair(pointsList);
        var actualLeft = actual.a;
        var actualRight = actual.b;

        var pairIsEquivalent =
                (!actualLeft.equals(actualRight))
                        && (actualLeft == expectedLeft || actualLeft == expectedRight)
                        && (actualRight == expectedRight || actualRight == expectedLeft);

        assertTrue(pairIsEquivalent);
    }



    @Test
    void findsClosestPair() {
        for (int i = 0; i < 50; i++) {
            var randomPoints = randomPoints(4096, 1000);
            var dumbFinder = new DumbFinder();
            var dumbResult = dumbFinder.findClosestPair(randomPoints);
            var smartResult = testFinder.findClosestPair(randomPoints);
            assertEquals(dumbResult.dist, smartResult.dist);
        }
    }

    @ParameterizedTest
    @CsvSource({"124", "256", "512", "1024", "4096", "8192"})
    void complexityLessThanLinearithmicTest(int n) {

        //In it's current form, I expect a complexity of O(N) = N * (log(N))^2
        long complexity = (long) (n * (Math.log(n)) * (Math.log(n)));
        long complexityLog10 = (long) Math.log10(complexity);

        long fastComplexity = (long) (n * (Math.log(n)));
        long fastComplexityLog10 = (long) Math.log10(fastComplexity);

        var points = randomPoints(n, 100.);
        testFinder.findClosestPair(points);
        var comparisons = testFinder.getLatestComparisons();
        var comparisonsLog10 = (long) Math.log10(comparisons);

        System.out.println(
                String.format(
                        "##############################################################\n" +
                                "For n = %d:\n" +
                                "(O_1(n) = n*(log(n)*log(n))) = %d\n" +
                                "log10(O_1(n)) = %d\n" +
                                "(O_2(n) = n*(log(n))) = %d\n" +
                                "log10(O_2(n)) = %d\n" +
                                "comparisons = %d\n" +
                                "log10(comparisons) = %d\n" +
                                "##############################################################\n",
                        n,
                        complexity,
                        complexityLog10,
                        fastComplexity,
                        fastComplexityLog10,
                        comparisons,
                        comparisonsLog10
                )
        );

        //Assert that the outcome is within one order of magnitude of N...
        assertTrue(complexityLog10 >= comparisonsLog10);
    }
}