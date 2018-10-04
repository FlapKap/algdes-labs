package util.jon;

import java.util.Objects;
import java.util.function.Function;

/**
 * Utility class for having triplets of values.
 *
 * @param <A> The type of the left value.
 * @param <B> The type of the middle value.
 * @param <C> The type of the right value.
 */
public class Triplet<A, B, C> {
    public final A left;
    public final B middle;
    public final C right;

    public Triplet(A left, B middle, C right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public Triplet<A, B, C> update(Function<Triplet<A, B, C>, Triplet<A, B, C>> updater) {
        return updater.apply(this);
    }

    public Triplet<A, B, C> updateLeft(Function<A, A> updater) {
        return new Triplet<>(updater.apply(left), middle, right);
    }

    public Triplet<A, B, C> updateMiddle(Function<B, B> updater) {
        return new Triplet<>(left, updater.apply(middle), right);
    }

    public Triplet<A, B, C> updateRight(Function<C, C> updater) {
        return new Triplet<>(left, middle, updater.apply(right));
    }

    public static <L, M, R> Triplet<L, M, R> of(L left, M middle, R right) {
        return new Triplet<>(left, middle, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triplet)) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(left, triplet.left) &&
                Objects.equals(middle, triplet.middle) &&
                Objects.equals(right, triplet.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, middle, right);
    }
}
