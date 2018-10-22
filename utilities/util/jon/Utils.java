package util.jon;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    @SafeVarargs
    public static <T> ArrayList<T> append(List<T> a, T ... elements) {
        var result = new ArrayList<T>(a.size() + elements.length);
        result.addAll(a);
        result.addAll(Arrays.asList(elements));
        return result;
    }

    public static <T> Set<T> concat(Set<T> a, Set<T> b) {
        var result = new TreeSet<T>(a);
        result.addAll(b);
        return result;
    }

    @SafeVarargs
    public static <T> Set<T> append(Set<T> a, T ... elements) {
        var result = new TreeSet<T>(a);
        result.addAll(Arrays.asList(elements));
        return result;
    }

    @SafeVarargs
    public static <T> ArrayList<T> arrayListOf(T ... elements) {
        var list = new ArrayList<T>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
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

    public static <T> Stream<Pair<T, T>> consecutive(Stream<T> stream) {
        List<Pair<T, T>> pairs = new LinkedList<>();
        stream.reduce((a, b) -> {
            pairs.add(Pair.of(a, b));
            return b;
        });
        return pairs.stream();
    }

    public static <A, B, R> Stream<R> zip(
            Stream<A> streamA,
            Stream<B> streamB,
            BiFunction<? super A, ? super B, R> function
    ) {
        boolean isParallel = streamA.isParallel() || streamB.isParallel(); // same as Stream.concat
        Spliterator<A> splitrA = streamA.spliterator();
        Spliterator<B> splitrB = streamB.spliterator();
        int characteristics =
                splitrA.characteristics()
                        & splitrB.characteristics()
                        & (Spliterator.SIZED | Spliterator.ORDERED);
        Iterator<A> itrA = Spliterators.iterator(splitrA);
        Iterator<B> itrB = Spliterators.iterator(splitrB);
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<R>(
                        Math.min(splitrA.estimateSize(), splitrB.estimateSize()), characteristics) {
                    @Override
                    public boolean tryAdvance(Consumer<? super R> action) {
                        if (itrA.hasNext() && itrB.hasNext()) {
                            action.accept(function.apply(itrA.next(), itrB.next()));
                            return true;
                        }
                        return false;
                    }
                },
                isParallel)
                .onClose(streamA::close)
                .onClose(streamB::close);
    }

    @SafeVarargs
    public static <T> Stream<List<T>> product(Stream<T>... streams) {
        if (streams.length == 0) {
            return Stream.empty();
        }
        List<List<T>> cartesian = streams[streams.length - 1].map(Collections::singletonList).collect(Collectors.toList());
        for (int i = streams.length - 2; i >= 0; i--) {
            final List<List<T>> previous = cartesian;
            cartesian = streams[i].flatMap(x -> previous.stream().map(p -> {
                final List<T> list = new ArrayList<T>(p.size() + 1);
                list.add(x);
                list.addAll(p);
                return list;
            })).collect(Collectors.toList());
        }
        return cartesian.stream();
    }
}
