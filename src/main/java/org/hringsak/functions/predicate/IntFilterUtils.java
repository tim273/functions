package org.hringsak.functions.predicate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collector;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hringsak.functions.predicate.IntPredicateUtils.intPredicate;
import static org.hringsak.functions.stream.IntStreamUtils.defaultIntStream;

/**
 * Convenience methods for filtering arrays of <code>int</code> primitives, without having to spell out the entire
 * stream-&gt;filter-&gt;collect process.
 */
public final class IntFilterUtils {

    private IntFilterUtils() {
    }

    /**
     * Filters an array of ints, based a predicate, into another int array.
     *
     * @param ints   An array of ints to be filtered.
     * @param predicate A predicate with which to filter the ints array.
     * @return An array of ints filtered from an array of int.
     */
    public static int[] intFilter(int[] ints, IntPredicate predicate) {
        return defaultIntStream(ints)
                .filter(predicate)
                .toArray();
    }

    /**
     * Filters an array of ints, based a predicate, into a set.
     *
     * @param ints   An array of ints to be filtered.
     * @param predicate A predicate with which to filter the ints array.
     * @return A Set of Integer objects filtered from an array of int.
     */
    public static Set<Integer> intFilterToSet(int[] ints, IntPredicate predicate) {
        return intFilter(ints, IntFilterCollector.of(predicate, toSet()));
    }

    /**
     * Filters an array of ints, based a predicate, into a list of distinct <code>Integer</code> instances (according to
     * <code>Integer.compare(int, int)</code>).
     *
     * @param ints   An array of ints to be filtered.
     * @param predicate A predicate with which to filter the ints array.
     * @return A List of Integer objects filtered from an array of int.
     */
    public static List<Integer> intFilterDistinct(int[] ints, IntPredicate predicate) {
        return defaultIntStream(ints)
                .filter(intPredicate(predicate))
                .distinct()
                .boxed()
                .collect(toList());
    }

    /**
     * Filters an array of ints, based a predicate, into a collection. This overload takes an object that is built
     * using the {@link #intFilterAndThen(IntPredicate, Collector)} method, which allows you to specify both a
     * <code>predicate</code> and a <code>Collector</code> to build any type of <code>Collection</code> as a result. For
     * example:
     * <pre>
     *     int[] ints = ...
     *     List&lt;Integer&gt; = IntFilterUtils.intFilter(ints, FilterUtils.intFilterAndThen(IntPredicateUtils.intGt(1.0D), Collectors.toCollection(LinkedList::new)));
     * </pre>
     * Or, with static imports:
     * <pre>
     *     intFilter(ints, intFilterAndThen(intGt(1.0D), toCollection(LinkedList::new)));
     * </pre>
     *
     * @param ints         An array of ints to be filtered.
     * @param filterCollector An object containing a predicate with which to filter the ints array, and a Collector
     *                        to accumulate results into a Collection.
     * @param <C>             The type of the resulting Collection.
     * @return A Collection of Integer objects filtered from an array of int.
     */
    public static <C extends Collection<Integer>> C intFilter(int[] ints, IntFilterCollector<C> filterCollector) {
        return defaultIntStream(ints)
                .filter(filterCollector.getFilter())
                .boxed()
                .collect(filterCollector.getCollector());
    }

    /**
     * Builds an object combining a <code>IntPredicate</code> and a <code>Collector</code> for use in the {@link
     * #intFilter(int[], IntFilterCollector)} method.
     *
     * @param filter    A Predicate for filtering elements from an array of ints.
     * @param collector A <code>Collector</code> to accumulate elements into a Collection of Integer objects.
     * @param <C>       The type of the resulting collection.
     * @return A Collection of Integer containing objects filtered from an array of int.
     */
    public static <C extends Collection<Integer>> IntFilterCollector<C> intFilterAndThen(IntPredicate filter, Collector<Integer, ?, C> collector) {
        return IntFilterCollector.of(filter, collector);
    }
}
