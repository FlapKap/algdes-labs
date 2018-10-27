package util.jon;

public class IntPair {
    public final int left;
    public final int right;

    public IntPair(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public static IntPair of(int left, int right) {
        return new IntPair(left, right);
    }

    public Pair<Integer, Integer> boxed() {
        return Pair.of(left, right);
    }
}
