package util.jon;

import java.util.function.BiFunction;
import java.util.function.Function;

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

    public IntPair update(BiFunction<Integer, Integer, IntPair> updater) {
        return updater.apply(left, right);
    }

    public IntPair updateLeft(Function<Integer, Integer> updater) {
        return IntPair.of(updater.apply(left), right);
    }

    public IntPair updateRight(Function<Integer, Integer> updater) {
        return IntPair.of(left, updater.apply(right));
    }

    public Pair<Integer, Integer> boxed() {
        return Pair.of(left, right);
    }

}
