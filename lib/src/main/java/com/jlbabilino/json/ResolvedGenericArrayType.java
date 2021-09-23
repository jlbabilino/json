package com.jlbabilino.json;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

class ResolvedGenericArrayType implements GenericArrayType {

    private final Type genericComponentType;

    ResolvedGenericArrayType(Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }

    @Override
    public Type getGenericComponentType() {
        return genericComponentType;
    }

    @Override
    public String toString() {
        return genericComponentType.toString() + "[]";
    }
}
