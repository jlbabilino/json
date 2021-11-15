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
