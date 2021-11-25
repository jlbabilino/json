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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * This internal implementation of {@link GenericArrayType} is used to represent
 * type information for arrays of parameterized types.
 * 
 * @see ResolvedTypes
 * @author Justin Babilino
 */
class ResolvedGenericArrayType implements GenericArrayType {

    /**
     * The component type
     */
    private final Type genericComponentType;

    /**
     * Constructs a {@code ResolvedGenericArrayType} with the component type.
     * 
     * @param genericComponentType the component type
     */
    ResolvedGenericArrayType(Type genericComponentType) {
        this.genericComponentType = genericComponentType;
    }

    /**
     * Gets the component type.
     * 
     * @return the component type
     */
    @Override
    public Type getGenericComponentType() {
        return genericComponentType;
    }

    /**
     * Represents this {@code GenericArrayType} as a string. The string is
     * constructed by attaching "[]" to the end of the string representation of the
     * component type. For example, the type {@code List&lt;String&gt;[]} becomes
     * "List<String>[]".
     * 
     * @return String
     */
    @Override
    public String toString() {
        return genericComponentType.toString() + "[]";
    }
}
