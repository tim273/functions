package org.hringsak.functions

import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.BiPredicate
import java.util.function.Function
import java.util.function.Predicate

import static java.util.function.Function.identity
import static org.hringsak.functions.PredicateUtils.*

class PredicateUtilsSpec extends Specification {

    def 'predicate passing null value throws NPE'() {

        when:
        predicate({ String s -> s.isEmpty() }).test(null)

        then:
        thrown(NullPointerException)
    }

    def 'predicate passing non-null returns expected value'() {
        expect:
        predicate({ String s -> s.isEmpty() }).test('')
    }

    @Unroll
    def 'predicate default passing parameter "#booleanParameter" and target "#target" returns #expected'() {

        expect:
        def predicate = { String s -> s.isEmpty() } as Predicate
        predicateDefault(predicate, defaultParameter).test(target) == expected

        where:
        defaultParameter | target | expected
        true             | null   | true
        true             | 'test' | false
        true             | ''     | true
        false            | null   | false
        false            | 'test' | false
        false            | ''     | true
    }

    @Unroll
    def 'predicate for bi-predicate passing constantValue "#constantValue" and target "#target" returns #expected'() {

        expect:
        def biPredicate = { a, b -> a.equals(b) } as BiPredicate
        predicate(biPredicate, constantValue).test(target) == expected

        where:
        constantValue | target | expected
        'test'        | null   | false
        'test'        | ''     | false
        'test'        | 'test' | true
        null          | 'test' | false
        ''            | 'test' | false
    }

    @Unroll
    def 'inverse predicate for bi-predicate passing constantValue "#constantValue" and target "#target" returns #expected'() {

        expect:
        def biPredicate = { a, b -> a.equals(b) } as BiPredicate
        inversePredicate(biPredicate, constantValue).test(target) == expected

        where:
        constantValue | target | expected
        'test'        | null   | false
        'test'        | ''     | false
        'test'        | 'test' | true
        null          | 'test' | false
        ''            | 'test' | false
    }

    @Unroll
    def 'predicate constant passing parameter "#booleanParameter" and target "#target" returns #expected'() {

        expect:
        predicateConstant(booleanParameter).test(target) == expected

        where:
        booleanParameter | target | expected
        true             | null   | true
        true             | 'test' | true
        false            | null   | false
        false            | 'test' | false
    }

    @Unroll
    def 'from mapper passing target "#target" returns #expected'() {

        expect:
        fromMapper({ String s -> !s.isEmpty() } as Function).test(target) == expected

        where:
        target | expected
        null   | false
        ''     | false
        'test' | true
    }

    def 'not passing null value throws NPE'() {

        when:
        not({ String s -> s.isEmpty() }).test(null)

        then:
        thrown(NullPointerException)
    }

    def 'not passing non-null returns expected value'() {
        expect:
        not({ String s -> s.isEmpty() }).test('test')
    }

    @Unroll
    def 'is equal passing constantValue #constantValue and target #target returns #expected'() {

        expect:
        def function = { String s -> s.toString() } as Function
        isEqual(constantValue, function).test(target) == expected

        where:
        constantValue | target | expected
        'test'        | null   | false
        'test'        | ''     | false
        'test'        | 'test' | true
        null          | 'test' | false
        ''            | 'test' | false
    }

    @Unroll
    def 'is not equal passing constantValue #constantValue and target #target returns #expected'() {

        expect:
        def function = { String s -> s.toString() } as Function
        isNotEqual(constantValue, function).test(target) == expected

        where:
        constantValue | target | expected
        'test'        | null   | true
        'test'        | ''     | true
        'test'        | 'test' | false
        null          | 'test' | true
        ''            | 'test' | true
    }

    @Unroll
    def 'equals ignore case passing constantValue #constantValue and target #target returns #expected'() {

        expect:
        def function = { String s -> s.toString() } as Function
        equalsIgnoreCase(constantValue, function).test(target) == expected

        where:
        constantValue | target | expected
        'test'        | null   | false
        'test'        | ''     | false
        'test'        | 'test' | true
        'test'        | 'TEST' | true
        'TEST'        | 'test' | true
        null          | 'test' | false
        ''            | 'test' | false
    }

    @Unroll
    def 'contains passing collection "#collection" and target "#target" returns "#expected"'() {

        expect:
        def function = { String s -> s.toString() } as Function
        contains(collection, function).test(target) == expected

        where:
        collection | target | expected
        ['test']   | null   | false
        ['test']   | ''     | false
        ['test']   | 'test' | true
        [null]     | 'test' | false
        [null]     | null   | false
        ['']       | 'test' | false
        []         | 'test' | false
    }

    @Unroll
    def 'inverse contains passing collection "#collection" and target "#target" returns "#expected"'() {

        expect:
        def function = { String s -> collection } as Function
        inverseContains(function, target).test(target) == expected

        where:
        collection | target | expected
        ['test']   | null   | false
        ['test']   | ''     | false
        ['test']   | 'test' | true
        [null]     | 'test' | false
        [null]     | null   | false
        ['']       | 'test' | false
        []         | 'test' | false
    }

    @Unroll
    def 'contains key for map #map passing #enumValue returns #expected'() {

        expect:
        def extractor = { TestValue t -> t.name() } as Function
        containsKey(map, extractor).test(enumValue) == expected

        where:
        map                            | enumValue     | expected
        null                           | TestValue.ONE | false
        [:]                            | TestValue.ONE | false
        TestValue.makeNameToValueMap() | null          | false
        TestValue.makeNameToValueMap() | TestValue.ONE | true
        TestValue.makeNameToValueMap() | TestValue.TWO | true
    }

    @Unroll
    def 'contains value for map #map passing #enumValue returns #expected'() {

        expect:
        def extractor = { TestValue t -> t.name() } as Function
        containsValue(map, extractor).test(enumValue) == expected

        where:
        map                            | enumValue     | expected
        null                           | TestValue.ONE | false
        [:]                            | TestValue.ONE | false
        TestValue.makeValueToNameMap() | null          | false
        TestValue.makeValueToNameMap() | TestValue.ONE | true
        TestValue.makeValueToNameMap() | TestValue.TWO | true
    }

    @Unroll
    def 'contains char with string extractor passing value "#extractedString" and "#searchChar" returns "#expected"'() {

        expect:
        containsChar(Function.identity(), searchChar.codePointAt(0)).test(extractedString) == expected

        where:
        extractedString | searchChar | expected
        null            | '\0'       | false
        null            | 'e'        | false
        'test'          | '\0'       | false
        'test'          | 'e'        | true
    }

    @Unroll
    def 'contains sequence with string extractor passing value "#extractedString" and "#searchSeq" returns "#expected"'() {

        expect:
        containsSequence(Function.identity(), searchSeq).test(extractedString) == expected

        where:
        extractedString | searchSeq | expected
        null            | null      | false
        null            | ''        | false
        null            | 'es'      | false
        'test'          | null      | false
        'test'          | ''        | true
        'test'          | 'es'      | true
    }

    @Unroll
    def 'contains ignore case with string extractor passing value "#extractedString" and "#searchSeq" returns "#expected"'() {

        expect:
        containsIgnoreCase(Function.identity(), searchSeq).test(extractedString) == expected

        where:
        extractedString | searchSeq | expected
        null            | null      | false
        null            | ''        | false
        null            | 'es'      | false
        null            | 'ES'      | false
        'TEST'          | null      | false
        'TEST'          | ''        | true
        'TEST'          | 'es'      | true
        'TEST'          | 'ES'      | true
    }

    @Unroll
    def 'is alpha passing value "#extractedString" returns "#expected"'() {

        expect:
        isAlpha(Function.identity()).test(extractedString) == expected

        where:
        extractedString | expected
        null            | false
        ''              | false
        'test '         | false
        'test1'         | false
        'test'          | true
        'TEST'          | true
    }

    @Unroll
    def 'is alphanumeric passing value "#extractedString" returns "#expected"'() {

        expect:
        isAlphanumeric(Function.identity()).test(extractedString) == expected

        where:
        extractedString | expected
        null            | false
        ''              | false
        'test '         | false
        'test1'         | true
        'test'          | true
        'TEST'          | true
        '123'           | true
        '123 '          | false
    }

    @Unroll
    def 'is numeric passing value "#extractedString" returns "#expected"'() {

        expect:
        isNumeric(Function.identity()).test(extractedString) == expected

        where:
        extractedString | expected
        null            | false
        ''              | false
        '123 '          | false
        '123a'          | false
        '1.23'          | false
        '+123'          | false
        '-123'          | false
        '1,234'         | false
        '123'           | true
    }

    @Unroll
    def 'starts with passing value "#extractedString" and "#prefix" returns "#expected"'() {

        expect:
        startsWith(Function.identity(), prefix).test(extractedString) == expected

        where:
        extractedString | prefix | expected
        null            | null   | false
        null            | ''     | false
        null            | 't'    | false
        'test'          | null   | false
        'test'          | ''     | true
        'test'          | 't'    | true
    }

    @Unroll
    def 'starts with ignore case passing value "#extractedString" and "#prefix" returns "#expected"'() {

        expect:
        startsWithIgnoreCase(Function.identity(), prefix).test(extractedString) == expected

        where:
        extractedString | prefix | expected
        null            | null   | false
        null            | ''     | false
        null            | 't'    | false
        'TEST'          | null   | false
        'TEST'          | ''     | true
        'TEST'          | 't'    | true
        'TEST'          | 'T'    | true
    }

    @Unroll
    def 'ends with passing value "#extractedString" and "#suffix" returns "#expected"'() {

        expect:
        endsWith(Function.identity(), suffix).test(extractedString) == expected

        where:
        extractedString | suffix | expected
        null            | null   | false
        null            | ''     | false
        null            | 't'    | false
        'test'          | null   | false
        'test'          | ''     | true
        'test'          | 't'    | true
    }

    @Unroll
    def 'ends with ignore case passing value "#extractedString" and "#suffix" returns "#expected"'() {

        expect:
        endsWithIgnoreCase(Function.identity(), suffix).test(extractedString) == expected

        where:
        extractedString | suffix | expected
        null            | null   | false
        null            | ''     | false
        null            | 't'    | false
        'TEST'          | null   | false
        'TEST'          | ''     | true
        'TEST'          | 't'    | true
        'TEST'          | 'T'    | true
    }

    @Unroll
    def 'is null passing value "#target" returns "#expected"'() {

        expect:
        def function = { AbstractMap.SimpleEntry e -> e.getValue() } as Function
        isNull(function).test(target) == expected

        where:
        target                                      | expected
        new AbstractMap.SimpleEntry("key", "value") | false
        new AbstractMap.SimpleEntry("key", null)    | true
        null                                        | true
    }

    @Unroll
    def 'not null passing value "#target" returns "#expected"'() {

        expect:
        def function = { AbstractMap.SimpleEntry e -> e.getValue() } as Function
        notNull(function).test(target) == expected

        where:
        target                                      | expected
        new AbstractMap.SimpleEntry("key", "value") | true
        new AbstractMap.SimpleEntry("key", null)    | false
        null                                        | false
    }

    @Unroll
    def 'gt passing #paramOne and #paramTwo returns #expected'() {
        expect:
        gt(paramOne as Comparable, identity()).test(paramTwo) == expected

        where:
        paramOne | paramTwo | expected
        null     | null     | false
        'a'      | ''       | true
        'a'      | 'a'      | false
        'a'      | 'b'      | false
        'b'      | 'a'      | true
        ''       | 'a'      | false
        1        | 2        | false
        2        | 1        | true
    }

    @Unroll
    def 'gte passing #paramOne and #paramTwo returns #expected'() {

        expect:
        gte(paramOne as Comparable, identity()).test(paramTwo) == expected

        where:
        paramOne | paramTwo | expected
        null     | null     | true
        'a'      | ''       | true
        'a'      | 'a'      | true
        'a'      | 'b'      | false
        'b'      | 'a'      | true
        ''       | 'a'      | false
        1        | 2        | false
        2        | 1        | true
    }

    @Unroll
    def 'lt passing #paramOne and #paramTwo returns #expected'() {

        expect:
        lt(paramOne as Comparable, identity()).test(paramTwo) == expected

        where:
        paramOne | paramTwo | expected
        null     | null     | false
        'a'      | ''       | false
        'a'      | 'a'      | false
        'a'      | 'b'      | true
        'b'      | 'a'      | false
        ''       | 'a'      | true
        1        | 2        | true
        2        | 1        | false
    }

    @Unroll
    def 'lte passing #paramOne and #paramTwo returns #expected'() {

        expect:
        PredicateUtils.lte(paramOne as Comparable, identity()).test(paramTwo) == expected

        where:
        paramOne | paramTwo | expected
        null     | null     | true
        'a'      | ''       | false
        'a'      | 'a'      | true
        'a'      | 'b'      | true
        'b'      | 'a'      | false
        ''       | 'a'      | true
        1        | 2        | true
        2        | 1        | false
    }

    @Unroll
    def 'is empty passing #target returns #expected'() {

        expect:
        def function = { List l -> l.asCollection() } as Function
        isEmpty(function).test(target) == expected

        where:
        target   | expected
        ['test'] | false
        null     | true
        []       | true
    }

    @Unroll
    def 'is not empty passing #target returns #expected'() {

        expect:
        def function = { List l -> l.asCollection() } as Function
        isNotEmpty(function).test(target) == expected

        where:
        target   | expected
        ['test'] | true
        null     | false
        []       | false
    }

    @Unroll
    def 'extract and filter passing input "#input"'() {

        expect:
        def predicate = isEqual(4, Function.identity())
        extractAndFilter({ String s -> s.length() } as Function, predicate).test(input) == expected

        where:
        input  | expected
        null   | false
        ''     | false
        'test' | true
    }
}
