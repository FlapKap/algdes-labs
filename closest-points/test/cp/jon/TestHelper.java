package cp.jon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestHelper {
    /**
     * Creates n random points for testing.
     *
     * @param n   the number of random points to generate.
     * @param mul multiply all generated coordinates with this value.
     * @return a list of random points.
     */
    public static List<Point> randomPoints(int n, double mul) {
        var random = new Random(System.currentTimeMillis());
        var outList = new ArrayList<Point>(n);
        for (int i = 0; i < n; i++) {
            outList.add(
                    new Point(
                            String.format("%d", i),
                            random.nextDouble() * mul,
                            random.nextDouble() * mul
                    )
            );
        }
        return outList;
    }
}
