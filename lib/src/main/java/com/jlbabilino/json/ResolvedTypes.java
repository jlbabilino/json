/*
 * Copyright (C) 2021 Justin Babilino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jlbabilino.json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

/**
 * This is a utility class used internally by the library. It deals with
 * conversions between different Java reflection types. A resolved type is a
 * Java {@code Type} that contains no type variables, and an unresolved type
 * contains at least one type variable.
 * 
 * @see JSONDeserializer
 * @author Justin Babilino
 */
final class ResolvedTypes {

    /**
     * Prevent Instantiation
     */
    private ResolvedTypes() {
    }

    /**
     * Gets the underlying {@link Class} type from a resolved {@link Type}. For
     * exmaple, if a {@link ParameterizedType} of List<Integer> were passed in,
     * {@code List.class} would be returned. If wildcard type is passed in, the
     * resovled class of the upper bound is returned. Of all Java implementations of
     * {@code Type}, only {@link TypeVariable} cannot be resolved to a
     * {@code Class}. For these cases, this method returns {@code null} because this
     * cannot happen in any use of this method in this library, and this method is
     * not public.
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
            // note: if there are no specified upper bounds, upper bound index 0 will be
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

    /**
     * <p>
     * Removes the type variables from an unresolved {@code Type} using a type
     * variable map to link type variables with resolved types. For example:
     * </p>
     * <p>
     * if {@code type} is a {@code ParameterizedType} corresponding to
     * {@code List<T>} and {@code typeVar} is a {@code TypeVariable} corresponding
     * to {@code T}:
     * </p>
     * 
     * <pre>
     * resolveType(type, Map.of(typeVar, String.class))
     * </pre>
     * <p>
     * returns {@code List<String>} as a {@code ParameterizedType}.
     * </p>
     * <p>
     * This method fails to resolve types if there are recursive type variables
     * (type variables that point to types that have other type variables) because
     * that situation would be impossible by design in this library. The reason is
     * that the main deserializer method has a parameter of type {@code Type}, which
     * must always be resolved. Each recursive call of the main deserializer method
     * always calls this method to remove type varaibles first so this method is
     * never called with recursive type variables.
     * 
     * @param type            the {@code Type} to resolve
     * @param typeVariableMap the map to link type variables to resolved types
     * @return the resolved type with no type variables.
     */
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

    /**
     * Resolves an unresolved {@code GenericArrayType} using a type variable map.
     * This method generates a new {@code GenericArrayType} that contains no type
     * variables, but recursive type variables will result in error. If the array
     * type can be expressed as a {@code Class}, it will be converted to that. For
     * example, if {@code T} is {@code int.class}, then {@code T[][]} would be
     * converted to {@code int[][].class}.
     * 
     * @param genericArrayType the {@code GenericArrayType} to resolve
     * @param typeVariableMap  the map to link type variables to resolved types
     * @return the resolved {@code GenericArrayType}
     */
    static Type resolveGenericArrayType(GenericArrayType genericArrayType, Map<TypeVariable<?>, Type> typeVariableMap) {
        return arrayOf(genericArrayType.getGenericComponentType(), typeVariableMap);
    }

    /**
     * Resolves an unresolved {@code GenericArrayType} using a type variable map.
     * This method generates a new {@code GenericArrayType} that contains no type
     * variables, but recursive type variables will result in error.
     * 
     * @param genericArrayType the {@code GenericArrayType} to resolve
     * @param typeVariableMap  the map to link type variables to resolved types
     * @return the resolved {@code ParameterizedType}
     */
    static ResolvedParameterizedType resolveParameterizedType(ParameterizedType parameterizedType,
            Map<TypeVariable<?>, Type> typeVariableMap) {
        Type ownerType;
        Class<?> rawType;
        Type[] resolvedTypeParameters;

        Type parameterizedTypeOwnerType = parameterizedType.getOwnerType();
        if (parameterizedTypeOwnerType != null) {
            ownerType = resolveType(parameterizedTypeOwnerType, typeVariableMap);
        } else {
            ownerType = null;
        }
        rawType = (Class<?>) parameterizedType.getRawType();
        Type[] unresolvedTypeParameters = parameterizedType.getActualTypeArguments();
        int typeParametersLength = unresolvedTypeParameters.length;
        resolvedTypeParameters = new Type[unresolvedTypeParameters.length];
        for (int i = 0; i < typeParametersLength; i++) {
            resolvedTypeParameters[i] = resolveType(unresolvedTypeParameters[i], typeVariableMap);
        }
        return new ResolvedParameterizedType(ownerType, rawType, resolvedTypeParameters);
    }

    /**
     * Resolves a type variable by mapping it to its resolved type in a type
     * variable map. Recursive type will result in an outputted type varaible, which
     * will not resolve the type, so don't put those in.
     * 
     * @param typeVariable    the type variable to resolve
     * @param typeVariableMap the map to link type variables with resolved types
     * @return the resolved type
     */
    static Type resolveTypeVariable(TypeVariable<?> typeVariable, Map<TypeVariable<?>, Type> typeVariableMap) {
        return typeVariableMap.get(typeVariable);
    }

    /**
     * Resolves an unresolved wildcard type by replacing type variables with mapped
     * types. Recursive type variables will result in an unresolved
     * {@code ResolvedWildcardType}, so don't do that.
     * 
     * @param wildcardType    the wildcard type to resolve
     * @param typeVariableMap the map to link type variables with resolved types
     * @return the resolved wildcard type
     */
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

    /**
     * Private method that generates the array type of a specified component
     * {@code Type}. It intelligently determines the appropriate form to put an
     * array in; an array that can be expressed as a {@code Class} will be expressed
     * that way, even if it was originally a {@code GenericArrayType}. It uses a
     * type variable map to resolve types.
     * 
     * @param type            component type of the new array type
     * @param typeVariableMap type variable map to resolve types
     * @return the array type
     */
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

    /**
     * Determines if the given type contains any type variables. It returns
     * {@code true} if there are no type variables, and {@code false} otherwise. In
     * this library, the word "resolved" is often used to mean that there are no
     * type variables in a Java type.
     * 
     * @param type the type to check
     * @return {@code true} if {@code type} contains no type variables, and
     *         {@code false} otherwise
     */
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

    /**
     * Determines if several types are ALL resolved. This method checks each type
     * with {@code isResolved(Type)}.
     * 
     * @param types the types to check
     * @return {@code true} if all types given are resolved, {@code false} if any
     *         are not resovled
     */
    private static boolean isResolved(Type... types) {
        for (Type type : types) {
            if (!isResolved(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Represents a Java type as a string.
     * 
     * @param type the type to convert to a string
     * @return the {@code String} representation
     */
    static String asString(Type type) {
        String str;
        if (type instanceof Class<?>) {
            str = ((Class<?>) type).getCanonicalName();
        } else {
            str = type.toString();
        }
        return str;
    }
}
