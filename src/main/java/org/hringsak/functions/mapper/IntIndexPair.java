package org.hringsak.functions.mapper;

import java.util.Objects;

public class IntIndexPair {

    private final int intValue;
    private final int index;

    private IntIndexPair(int intValue, int index) {
        this.intValue = intValue;
        this.index = index;
    }

    public static IntIndexPair of(int left, int right) {
        return new IntIndexPair(left, right);
    }

    public int getIntValue() {
        return intValue;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        IntIndexPair other = (IntIndexPair) obj;
        return Objects.equals(intValue, other.intValue) &&
                index == other.index;
    }

    @Override
    public String toString() {
        String identity = Integer.toHexString(System.identityHashCode(this));
        String template = "%s@%s[intValue=%s,index=%s]";
        return String.format(template, getClass().getName(), identity, intValue, index);
    }
}
