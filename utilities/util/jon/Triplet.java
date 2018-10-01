package util.jon;

import java.util.Objects;

/**
 * Utility class for having triplets of values.
 * @param <A> The type of the left value.
 * @param <B> The type of the middle value.
 * @param <C> The type of the right value.
 */
public class Triplet<A, B, C> {
    final A left;
    final B middle;
    final C right;

    public Triplet(A left, B middle, C right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
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
