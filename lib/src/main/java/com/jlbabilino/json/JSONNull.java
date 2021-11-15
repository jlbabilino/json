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

/**
 * <p>
 * This class wraps a <code>null</code> value in a <code>JSONEntry</code>. A
 * JSON {@code null} is essentially the same as a Java {@code null}, it
 * possesses no information, and no {@code null} is any different than another.
 * For this reason, there is only ever <i>one</i> instance of this class, and it
 * can be accessed through the field {@code JSONNull.NULL}.
 * <p>
 * Because of this property, all {@code JSONNull} values can be correctly
 * compared using the {@code ==} operator. To check if a {@code JSONEntry} is
 * {@code null}, you can either use {@code isNull()} or {@code == JSONNull.NULL}.
 *
 * @author Justin Babilino
 */
public class JSONNull extends JSONEntry {

    /**
     * Represents a {@code null} value in a JSON
     */
    public static final JSONNull NULL = new JSONNull();

    /**
     * Constructs a <code>NullJSONEntry</code>. This is marked as {@code private}
     * because creating another instance would violate the design of this
     * {@code class}. Since {@code JSONNull} does not carry any information, and no
     * instance can be distinguished from another, there is only ever one instance,
     * and that is JSONNull.NULL.
     */
    private JSONNull() {
    }

    /**
     * Returns {@code true} since this entry is a null.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isNull() {
        return true;
    }

    /**
     * Returns {@code JSONType.NULL} since this entry is a null.
     * 
     * @return {@code JSONType.NULL}
     */
    @Override
    public JSONType getType() {
        return JSONType.NULL;
    }

    /**
     * Returns "null", the null literal in JSON.
     * 
     * @param indentLevel this parameter has no effect on the result or effect of this implementation of this method
     * @param jsonFormat this parameter has no effect on the result or effect of this implementation of this method
     * @return "null"
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return "null";
    }
}
