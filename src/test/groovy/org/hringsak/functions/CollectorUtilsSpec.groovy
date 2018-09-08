package org.hringsak.functions

import com.google.common.base.Strings
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification

import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream

import static java.util.stream.Collectors.*
import static org.hringsak.functions.CollectorUtils.*

class CollectorUtilsSpec extends Specification {

    def 'conditional group by collector'() {
        given:
        def pairStream = Stream.of(pair(1, null), pair(1, ''), pair(2, 'stringOne'), pair(2, 'stringTwo'))
        def nonEmptyPredicate = { String str -> !Strings.isNullOrEmpty(str) } as Predicate
        def leftFunction = { Pair pair -> pair.getLeft() } as Function
        def rightFunction = { Pair pair -> pair.getRight() } as Function

        when:
        def stringByIntMap = pairStream.collect(groupingBy(leftFunction, mapping(rightFunction, conditionalCollector(nonEmptyPredicate))))

        then:
        with(stringByIntMap) {
            get(1) == []
            get(2) == ['stringOne', 'stringTwo']
        }
    }

    def 'to map from entry'() {
        given:
        def sourceMap = [keyOne: 'valueOne', keyTwo: 'valueTwo', keyThree: 'valueThree']

        when:
        def actual = sourceMap.entrySet().stream().collect(CollectorUtils.toMapFromEntry())

        then:
        actual == sourceMap
    }

    def 'to partitioned stream'() {
        given:
        def partitionSize = 10
        def elements = (1..100).collect({ "element${StringUtils.leftPad("$it", 3, '0')}" })

        when:
        def partitions = elements.stream().collect(toPartitionedStream(partitionSize)).collect(toList()) as Collection<List>

        then:
        partitions.size() == 10
        partitions.each { List partition -> partition.size() == partitionSize }
    }

    def 'to partitioned list'() {
        given:
        def partitionSize = 10
        def elements = (1..100).collect({ "element${StringUtils.leftPad("$it", 3, '0')}" })

        when:
        def partitions = elements.stream().collect(CollectorUtils.toPartitionedList(partitionSize)) as Collection<List>

        then:
        partitions.size() == 10
        partitions.each { List partition -> partition.size() == partitionSize }
    }

    def 'to string builder'() {
        when:
        def actual = 'test123'.codePoints()
                .filter { codePoint -> Character.isDigit(codePoint) }
                .boxed()
                .collect(CollectorUtils.toStringBuilder())
                .toString()

        then:
        actual == '123'
    }

    def 'to enum set'() {
        expect:
        Arrays.stream(TestValue.values()).collect(CollectorUtils.toEnumSet(TestValue)) == EnumSet.allOf(TestValue)
    }

    def 'to enum set passing null enum class'() {
        when:
        [].stream().collect(CollectorUtils.toEnumSet(null))

        then:
        def e = thrown(NullPointerException)
        e.message =~ '"enumClass"'
    }

    def 'to list with default for empty stream'() {
        expect:
        [].stream().collect(CollectorUtils.toListWithDefault('default')) == ['default']
    }

    def 'to list with default for populated stream'() {
        expect:
        ['test'].stream().collect(CollectorUtils.toListWithDefault('default')) == ['test']
    }

    def 'to set with default passing supplier for empty stream'() {
        expect:
        [].stream().collect(CollectorUtils.toSetWithDefault('default')) == ['default'] as Set
    }

    def 'to set with default passing supplier for populated stream'() {
        expect:
        ['test'].stream().collect(CollectorUtils.toSetWithDefault('default')) == ['test'] as Set
    }

    def 'with default passing supplier for empty stream'() {
        expect:
        [].stream().collect(CollectorUtils.withDefault('default', { new HashSet<>() })) == ['default'] as Set
    }

    def 'collect with default passing collector for populated stream'() {
        expect:
        ['test'].stream().collect(CollectorUtils.withDefault('default', { new HashSet<>() })) == ['test'] as Set
    }

    def 'to unmodifiable list'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toUnmodifiableList())
        result.add(4)

        then:
        thrown(UnsupportedOperationException)
        result instanceof List
    }

    def 'to unmodifiable set'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toUnmodifiableSet())
        result.add(4)

        then:
        thrown(UnsupportedOperationException)
        result instanceof Set
    }

    def 'to unmodifiable collection'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toUnmodifiableCollection { new LinkedList<>() })
        result.add(4)

        then:
        thrown(UnsupportedOperationException)
        result instanceof Collection
    }

    def 'to synchronized list'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toSynchronizedList())

        then:
        result.getClass().getSimpleName() == 'SynchronizedRandomAccessList'
    }

    def 'to synchronized set'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toSynchronizedSet())

        then:
        result.getClass().getSimpleName() == 'SynchronizedSet'
    }

    def 'to synchronized collection'() {
        given:
        def numStream = Stream.of(1, 2, 3)

        when:
        def result = numStream.collect(toSynchronizedCollection { new LinkedList<>() })

        then:
        result.getClass().getSimpleName() == 'SynchronizedCollection'
    }

    def pair(key, value) {
        Pair.of(key, value)
    }
}
