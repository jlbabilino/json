package com.jlbabilino.json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

final class ResolvedTypes {
    private ResolvedTypes() {
    }

    /**
     * Gets the underlying {@link Class} type from a {@link Type}. For exmaple, if a
     * {@link ParameterizedType} of List<Integer> were passed in, List.class would
     * be returned. If wildcard type is passed in, the resovled class of the upper
     * bound is returned. Of all Java implementations of {@code Type}, only
     * {@link TypeVariable} cannot be resolved to a {@code Class}. For these cases,
     * this method returns {@code null} because this cannot happen in any use of
     * this method in this library, and this method is not public.
     * 
     * @param type the type to be resolved to a {@code Class}
     * @return the resolved {@code Class}
     */
    static Class<?> resolveClass(Type type) {
        Class<?> resolvedClass;
        if (type instanceof GenericArrayType) {
            Class<?> componentType = resolveClass(((GenericArrayType) type).getGenericComponentType());
            resolvedClass = Array.newInstance(componentType, 0).getClass();
            // use Array.newInstance instead of Class.arrayType for compatibility
        } else if (type instanceof ParameterizedType) {
            resolvedClass = (Class<?>) ((ParameterizedType) type).getRawType();
            // not checking type variables, should be resolved before using this method
        } else if (type instanceof WildcardType) {
            // note if there are no specified upper bounds, upper bound index 0 will be
            // Object
            resolvedClass = resolveClass(((WildcardType) type).getUpperBounds()[0]);
        } else if (type instanceof Class<?>) {
            resolvedClass = (Class<?>) type;
        } else {
            // there's no sense throwing an exception because this isn't a public method
            // this code will NEVER excecute, but is needed for compile checks
            resolvedClass = null;
        }
        return resolvedClass;
    }

    static Type resolveType(Type type, Map<TypeVariable<?>, Type> typeVariableMap) {
        Type resolvedType;
        if (type instanceof Class<?>) {
            resolvedType = type;
        } else if (type instanceof GenericArrayType) {
            resolvedType = resolveGenericArrayType((GenericArrayType) type, typeVariableMap);
        } else if (type instanceof ParameterizedType) {
            resolvedType = resolveParameterizedType((ParameterizedType) type, typeVariableMap);
        } else if (type instanceof TypeVariable<?>) {
            resolvedType = resolveTypeVariable((TypeVariable<?>) type, typeVariableMap);
        } else /* if (type instanceof WildcardType) */ {
            resolvedType = resolveWildcardType((WildcardType) type, typeVariableMap);
        }
        return resolvedType;
    }

    static Type resolveGenericArrayType(GenericArrayType genericArrayType, Map<TypeVariable<?>, Type> typeVariableMap) {
        return arrayOf(genericArrayType.getGenericComponentType(), typeVariableMap);
    }

    static ResolvedParameterizedType resolveParameterizedType(ParameterizedType parameterizedType,
            Map<TypeVariable<?>, Type> typeVariableMap) {
        Type ownerType;
        Type rawType;
        Type[] resolvedTypeParameters;

        Type parameterizedTypeOwnerType = parameterizedType.getOwnerType();
        if (parameterizedTypeOwnerType != null) {
            ownerType = resolveType(parameterizedTypeOwnerType, typeVariableMap);
        } else {
            ownerType = null;
        }
        rawType = parameterizedType.getRawType();
        Type[] unresolvedTypeParameters = parameterizedType.getActualTypeArguments();
        int typeParametersLength = unresolvedTypeParameters.length;
        resolvedTypeParameters = new Type[unresolvedTypeParameters.length];
        for (int i = 0; i < typeParametersLength; i++) {
            resolvedTypeParameters[i] = resolveType(unresolvedTypeParameters[i], typeVariableMap);
        }
        return new ResolvedParameterizedType(ownerType, rawType, resolvedTypeParameters);
    }

    static Type resolveTypeVariable(TypeVariable<?> typeVariable, Map<TypeVariable<?>, Type> typeVariableMap) {
        return typeVariableMap.get(typeVariable);
    }

    static ResolvedWildcardType resolveWildcardType(WildcardType wildcardType,
            Map<TypeVariable<?>, Type> typeVariableMap) {
        Type[] lowerBounds = wildcardType.getLowerBounds();
        Type[] upperBounds = wildcardType.getUpperBounds();
        int lowerBoundsLength = lowerBounds.length;
        int upperBoundsLength = upperBounds.length;
        Type[] resolvedLowerBounds = new Type[lowerBoundsLength];
        Type[] resolvedUpperBounds = new Type[upperBoundsLength];
        for (int i = 0; i < lowerBoundsLength; i++) {
            resolvedLowerBounds[i] = resolveType(lowerBounds[i], typeVariableMap);
        }
        for (int i = 0; i < upperBoundsLength; i++) {
            resolvedUpperBounds[i] = resolveType(upperBounds[i], typeVariableMap);
        }
        return new ResolvedWildcardType(resolvedLowerBounds, resolvedUpperBounds);
    }

    private static Type arrayOf(Type type, Map<TypeVariable<?>, Type> typeVariableMap) {
        Type arrayType;
        if (type instanceof GenericArrayType) {
            arrayType = arrayOf(((GenericArrayType) type).getGenericComponentType(), typeVariableMap);
        } else if (type instanceof ParameterizedType) {
            arrayType = new ResolvedGenericArrayType(resolveType(type, typeVariableMap));
        } else if (type instanceof TypeVariable<?>) {
            arrayType = arrayOf(resolveType(type, typeVariableMap), typeVariableMap);
        } else if (type instanceof Class<?>) {
            Class<?> componentType = ((Class<?>) type);
            arrayType = Array.newInstance(componentType, 0).getClass();
        } else {
            arrayType = null;
        }
        return arrayType;
    }

    static boolean isResolved(Type type) {
        if (type instanceof GenericArrayType) {
            return isResolved(((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null && !isResolved(ownerType)) {
                return false;
            }
            return isResolved(parameterizedType.getActualTypeArguments());
        } else if (type instanceof TypeVariable<?>) {
            return false; // lol that was easy
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            if (!isResolved(wildcardType.getLowerBounds())) {
                return false;
            }
            if (!isResolved(wildcardType.getUpperBounds())) {
                return false;
            }
            return true;
        } else if (type instanceof Class<?>) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isResolved(Type... types) {
        for (Type type : types) {
            if (!isResolved(type)) {
                return false;
            }
        }
        return true;
    }
}
