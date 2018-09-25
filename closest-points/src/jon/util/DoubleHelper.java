package jon.util;

public class DoubleHelper {
    private static double EPSILON = 0.0000001;

    public static boolean doubleEq(double d1, double d2) {
        return (d1 == d2) || Math.abs(d1 - d2) < EPSILON;
    }
}
