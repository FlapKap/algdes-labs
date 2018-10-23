package util.jon;

/**
 * A class for uniform pairs.
 *
 * @param <T> the type of both entries in the pair.
 */
public class UPair<T> extends Pair<T, T> {

    public UPair(T left, T right) {
        super(left, right);
    }

    public static <T> UPair<T> from(T left, T right) {
        return new UPair<>(left, right);
    }
}
