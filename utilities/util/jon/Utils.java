package util.jon;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A class for containing static utility functions.
 */
public class Utils {

    /**
     * Concatenates two lists together, creating an ArrayList of the combined entries.
     *
     * @param a   the list that will make up the first list of entries of the concatenated list
     * @param b   the list that will make up the second list of entries of the concatenated list
     * @param <T> the type of elements that the lists contain
     * @return the two lists, concatenated
     */
    public static <T> ArrayList<T> concat(List<T> a, List<T> b) {
        ArrayList<T> result = new ArrayList<>(a.size() + b.size());
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    /**
     * Sorts a list by a particular key.
     *
     * @param list         the list to sort
     * @param keyExtractor a function that takes an element and returns its key
     * @param <T>          the type of the elements
     * @param <U>          the type of the key
     * @return a new, sorted, list object
     */
    public static <T, U extends Comparable<U>> List<T> sorted(List<T> list, Function<T, U> keyExtractor) {
        var result = new ArrayList<>(list);
        result.sort((p, q) -> {
            var pKey = keyExtractor.apply(p);
            var qKey = keyExtractor.apply(q);
            return pKey.compareTo(qKey);
        });
        return result;
    }

    /**
     * Sequential fold.
     * A slow alternative to a call to collect(), but doesn't require a state combinator.
     *
     * @param initialState the initial state
     * @param iterator     the iterator to fold over
     * @param folder       the function to fold with
     * @param <S>          the type of the accumulated object
     * @param <T>          the type of the elements in the stream
     * @return the accumulated object
     */
    public static <S, T> S fold(S initialState, Iterator<T> iterator, BiFunction<S, T, S> folder) {
        S currentState = initialState;
        while (iterator.hasNext()) {
            T t = iterator.next();
            currentState = folder.apply(currentState, t);
        }
        return currentState;
    }

    /**
     * Sequential fold.
     * A slow alternative to a call to collect(), but doesn't require a state combinator.
     *
     * @param initialState the initial state
     * @param stream       the stream to fold over
     * @param folder       the function to fold with
     * @param <S>          the type of the accumulated object
     * @param <T>          the type of the elements in the stream
     * @return the accumulated object
     */
    public static <S, T> S fold(S initialState, Stream<T> stream, BiFunction<S, T, S> folder) {
        return fold(initialState, stream.iterator(), folder);
    }

    /**
     * Folds over a list's entries.
     *
     * @param initialState the initial state
     * @param list         the collection to fold over
     * @param folder       the function to fold with
     * @param <S>          the type of the accumulated object
     * @param <T>          the type of elements in the list
     * @return the accumulated object
     */
    public static <S, T> S fold(S initialState, List<T> list, BiFunction<S, T, S> folder) {
        return fold(initialState, list.iterator(), folder);
    }

    /**
     * Folds over an array's entries.
     *
     * @param initialState the initial state
     * @param array        the array to fold over
     * @param folder       the function to fold with
     * @param <S>          the type of the accumulated object
     * @param <T>          the type of elements in the array
     * @return the accumulated object
     */
    public static <S, T> S fold(S initialState, T[] array, BiFunction<S, T, S> folder) {
        return fold(initialState, Arrays.stream(array).iterator(), folder);
    }
}
