package com.jlbabilino.json;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

class ResolvedWildcardType implements WildcardType {

    private final Type[] lowerBounds;
    private final Type[] upperBounds;

    ResolvedWildcardType(Type[] lowerBounds, Type[] upperBounds) {
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    @Override
    public Type[] getUpperBounds() {
        return upperBounds;
    }

    @Override
    public String toString() {
        return "?" + " haven't finished yet.";
    }
}
