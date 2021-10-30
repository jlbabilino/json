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
 * This class wraps a {@code boolean} value in a {@code JSONEntry}.
 *
 * @see JSONEntry
 * @see JSONNumber
 * @see JSONString
 * @author Justin Babilino
 */
public class JSONBoolean extends JSONEntry {

    /**
     * The {@code boolean} value in the entry.
     */
    private final boolean value;

    /**
     * Constructs a {@code JSONBoolean} with a {@code boolean} value to be stored in
     * the entry.
     *
     * @param value the <code>boolean</code> value to be stored in this entry
     */
    public JSONBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Returns the {@code boolean} value of this JSON boolean.
     * 
     * @return the {@code boolean} value of this JSON boolean
     */
    public boolean getBoolean() {
        return value;
    }

    /**
     * Returns {@code true} since this entry is a boolean.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isBoolean() {
        return true;
    }

    /**
     * Returns {@code JSONType.BOOLEAN} since this entry is a boolean.
     * 
     * @return {@code JSONType.BOOLEAN}
     */
    @Override
    public JSONType getType() {
        return JSONType.BOOLEAN;
    }

    /**
     * Generates a {@code String} that represents this JSON boolean. If this entry contains a {@code true} value,
     * then returns "true", otherwise returns "false". It is equivalent to
     * <pre>
     * Boolean.toString(getBoolean())
     * </pre>
     *
     * @param indentLevel this parameter has no effect on the result or effect of this implementation of this method.
     * @param format this parameter has no effect on the result or effect of this implementation of this method.
     * @return a {@code String} representing this JSON boolean.
     */
    @Override
    public String getJSONText(int indentLevel, JSONFormat format) {
        return Boolean.toString(value);
    }
}
