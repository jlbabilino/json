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

import static com.jlbabilino.json.ResolvedTypes.asString;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This internal implementation of {@link ParameterizedType} is used with
 * {@link ResolvedTypes} to generate generic type information for generic
 * parameterized types. It behaves exacly like a Java
 * {@code ParameterizedTypeImpl}, but it can be constructed.
 * 
 * @see ResolvedTypes
 * @author Justin Babilino
 */
class ResolvedParameterizedType implements ParameterizedType {

    /**
     * The generic owner type of this parameterized type; it is used for nested
     * classes where the owner class has a type parameter
     */
    private final Type ownerType;
    /**
     * The class that declared this parameterized type
     */
    private final Class<?> rawType;
    /**
     * The type arguments passed into the type parameters of this parameterized type
     */
    private final Type[] typeParameterArguments;

    /**
     * Constructs a {@code ResolvedParameterizedType} with the owner type, raw type,
     * and type parameters.
     * 
     * @param ownerType              the owner type
     * @param rawType                the raw type
     * @param typeParameterArguments the type parameters
     */
    ResolvedParameterizedType(Type ownerType, Class<?> rawType, Type[] typeParameterArguments) {
        this.ownerType = ownerType;
        this.rawType = rawType;
        this.typeParameterArguments = typeParameterArguments;
    }

    /**
     * Gets the owner type.
     * 
     * @return the owner type
     */
    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    /**
     * Gets the raw type.
     * 
     * @reutrn the raw type as a {@code Class}
     */
    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    /**
     * Gets the type parameter arguments.
     * 
     * @return the type parameter arguments
     */
    @Override
    public Type[] getActualTypeArguments() {
        return typeParameterArguments;
    }

    /**
     * Generates a string representing this {@code ParameterizedType}. The resulting
     * string appears the same as it were expressed in Java code. For example, if
     * there were a package called {@code pkg}, containing a class called
     * {@code Cls} having two type parameters, each with arguments {@code String}
     * and {@code Double} respectively, then a {@code ResolvedParameterizedType}
     * representing this specific type would have a {@code toString()} output of
     * "pkg.Cls&lt;java.lang.String, java.lang.Double&gt;".
     * 
     * @return the string representation of this {@code ParameterizedType}
     */
    @Override
    public String toString() {
        String ownerString = "";
        if (ownerType != null) {
            ownerString = asString(ownerType) + ".";
        }
        String rawString = asString(rawType);
        StringBuilder paramStringBuilder = new StringBuilder("<");
        for (Type parameterArgument : typeParameterArguments) {
            paramStringBuilder.append(asString(parameterArgument)).append(", ");
        }
        String paramString = paramStringBuilder.substring(0, paramStringBuilder.length() - 2) + ">";
        return ownerString + rawString + paramString;
    }
}