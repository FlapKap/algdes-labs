package util.jon;

/**
 * A class for uniform triplets, triplets where all contents are the same type.
 * Good for keeping triplets briefer.
 *
 * @param <T> the type for every slot in the triplet.
 */
public class UTriplet<T> extends Triplet<T, T, T> {
    public UTriplet(T left, T middle, T right) {
        super(left, middle, right);
    }

    public static <T> UTriplet<T> from(T left, T middle, T right) {
        return new UTriplet<>(left, middle, right);
    }
}
