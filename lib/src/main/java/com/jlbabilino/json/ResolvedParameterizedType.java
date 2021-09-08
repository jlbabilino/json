package com.jlbabilino.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

class ResolvedParameterizedType implements ParameterizedType {

    private final Type ownerType;
    private final Type rawType;
    private final Type[] resolvedTypeParameters;

    ResolvedParameterizedType(Type ownerType, Type rawType, Type[] resolvedTypeParameters) {
        this.ownerType = ownerType;
        this.rawType = rawType;
        this.resolvedTypeParameters = resolvedTypeParameters;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return resolvedTypeParameters;
    }

    @Override
    public String toString() {
        String ownerString = "";
        if (ownerType != null) {
            ownerString = ownerType.toString() + ".";
        }
        String paramString = Arrays.toString(resolvedTypeParameters);
        paramString = paramString.substring(1, paramString.length() - 1);
        return ownerString + rawType.toString() + "<" + paramString + ">";
    }
}
