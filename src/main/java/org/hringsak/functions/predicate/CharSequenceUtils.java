package org.hringsak.functions.predicate;

import java.util.function.IntPredicate;

import static org.hringsak.functions.predicate.IntPredicateUtils.isIntEqual;

final class CharSequenceUtils {

    private CharSequenceUtils() {
    }

    static boolean equals(CharSequence left, CharSequence right) {
        return new CharSequenceEqualityEvaluator(left, right).equals();
    }

    static boolean equalsIgnoreCase(CharSequence left, CharSequence right) {
        return new CharSequenceEqualityEvaluator(left, right).equalsIgnoreCase();
    }

    static boolean contains(CharSequence sequence, int searchChar) {
        return sequence != null && sequence.codePoints()
                .anyMatch(isIntEqual(searchChar));
    }

    static boolean contains(CharSequence sequence, CharSequence searchSequence) {
        return new CharSequenceMatcher(sequence, searchSequence)
                .containsSearchSequence();
    }

    static boolean containsIgnoreCase(CharSequence sequence, CharSequence searchSequence) {
        return new CharSequenceMatcher(sequence, searchSequence)
                .containsSearchSequenceIgnoreCase();
    }

    static boolean isAlpha(CharSequence sequence) {
        return isCharacterMatch(sequence, Character::isLetter);
    }

    private static boolean isCharacterMatch(CharSequence sequence, IntPredicate charPredicate) {
        return !isNullOrEmpty(sequence) &&
                sequence.codePoints().allMatch(charPredicate);
    }

    static boolean isAlphaNumeric(CharSequence sequence) {
        return isCharacterMatch(sequence, Character::isLetterOrDigit);
    }

    static boolean isNumeric(CharSequence sequence) {
        return isCharacterMatch(sequence, Character::isDigit);
    }

    static boolean startsWith(CharSequence sequence, CharSequence prefix) {
        return (sequence == prefix) ||
                (neitherAreNull(sequence, prefix) &&
                        sequence.length() >= prefix.length() &&
                        equals(getStartsWithSubSequence(sequence, prefix), prefix));
    }

    static boolean neitherAreNull(CharSequence left, CharSequence right) {
        return left != null && right != null;
    }

    private static CharSequence getStartsWithSubSequence(CharSequence sequence, CharSequence prefix) {
        return sequence.subSequence(0, prefix.length());
    }

    static boolean startsWithIgnoreCase(CharSequence sequence, CharSequence prefix) {
        return (sequence == prefix) ||
                (neitherAreNull(sequence, prefix) &&
                        sequence.length() >= prefix.length() &&
                        equalsIgnoreCase(getStartsWithSubSequence(sequence, prefix), prefix));
    }

    static boolean endsWith(CharSequence sequence, CharSequence suffix) {
        return (sequence == suffix) ||
                (neitherAreNull(sequence, suffix) &&
                        sequence.length() >= suffix.length() &&
                        equals(getEndsWithSubSequence(sequence, suffix), suffix));
    }

    private static CharSequence getEndsWithSubSequence(CharSequence sequence, CharSequence suffix) {
        int len = sequence.length();
        return sequence.subSequence(len - suffix.length(), len);
    }

    static boolean endsWithIgnoreCase(CharSequence sequence, CharSequence suffix) {
        return (sequence == suffix) ||
                (neitherAreNull(sequence, suffix) &&
                        sequence.length() >= suffix.length() &&
                        equalsIgnoreCase(getEndsWithSubSequence(sequence, suffix), suffix));
    }

    static boolean isNullOrEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }
}
