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
    JSONBoolean(boolean value) {
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
     * Attempts to convert this JSON boolean to a JSON object. Converting
     * from JSON boolean to JSON object is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONObject asObject() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON boolean to JSON object");
    }

    /**
     * Attempts to convert this JSON boolean to a JSON array. Converting
     * from JSON boolean to JSON array is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONArray asArray() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON boolean to JSON array");
    }

    /**
     * Attempts to convert this JSON boolean to a JSON boolean. Since this
     * is a JSON boolean, this JSON boolean is returned.
     * 
     * @return this JSON boolean
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONBoolean asBoolean() throws JSONConversionException {
        return this;
    }

    /**
     * Attempts to convert this JSON boolean to a JSON number. A JSON boolean
     * value of {@code false} converts to a JSON number of value {@code 0},
     * and a JSON boolean of value {@code true} converts to a JSON number of
     * ov value {@code 1}.
     * 
     * @return the numerical representation of this JSON boolean
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONNumber asNumber() throws JSONConversionException {
        return getBoolean() ? new JSONNumber(1) : new JSONNumber(0);
    }

    /**
     * Attempts to convert this JSON boolean to a JSON string. Conversion to
     * string is achieved using {@code java.lang.Boolean.toString(boolean)}
     * by passing in {@code getBoolean()} and wrapping the value in a
     * {@code JSONString}.
     * 
     * @return the string representation of this JSON boolean
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONString asString() throws JSONConversionException {
        return new JSONString(Boolean.toString(getBoolean()));
    }

    /**
     * Generates a {@code String} that represents this JSON boolean. If this entry contains a {@code true} value,
     * then returns "true", otherwise returns "false". It is equivalent to
     * <pre>
     * Boolean.toString(getBoolean())
     * </pre>
     *
     * @param indentLevel this parameter has no effect on the result or effect of this implementation of this method
     * @param jsonFormat this parameter has no effect on the result or effect of this implementation of this method
     * @return a {@code String} representing this JSON boolean
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return Boolean.toString(value);
    }

    /**
     * the false JSON boolean value
     */
    public static final JSONBoolean FALSE = new JSONBoolean(false);
    /**
     * the true JSON boolean value
     */
    public static final JSONBoolean TRUE = new JSONBoolean(true);

    @Override
    public int hashCode() {
        return Boolean.hashCode(getBoolean());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JSONBoolean) {
            return getBoolean() == ((JSONBoolean) obj).getBoolean();
        } else {
            return false;
        }
    }
}
