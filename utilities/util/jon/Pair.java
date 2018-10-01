package util.jon;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Utility class for holding pairs.
 * @param <A> Whatever you want in the left side of the pair.
 * @param <B> Whatever you want in the right side of the pair.
 */
public class Pair<A, B> {
    public final A left;
    public final B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public Pair<A, B> updateLeft(BiFunction<A, B, A> updater) {
        return new Pair<>(updater.apply(this.left, this.right), this.right);
    }

    public Pair<A, B> updateRight(BiFunction<A, B, B> updater) {
        return new Pair<>(this.left, updater.apply(this.left, this.right));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
