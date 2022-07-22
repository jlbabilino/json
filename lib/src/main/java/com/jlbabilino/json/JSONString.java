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
 * This class wraps a {@code String} value in a {@link JSONEntry}.
 * 
 * @author Justin Babilino
 */
public class JSONString extends JSONEntry {

    /**
     * The {@code String} value
     */
    private final String string;

    /**
     * Constructs a {@code JSONString} with a {@code String} value.
     * 
     * @param string the String value
     */
    JSONString(String string) {
        this.string = string;
    }

    /**
     * Returns the {@code String} value of this JSON string.
     * 
     * @return the {@code String} value of this JSON string
     */
    public String getString() {
        return string;
    }

    /**
     * Returns {@code true} since this entry is a string.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isString() {
        return true;
    }

    /**
     * Returns {@code JSONType.STRING} since this entry is a string.
     * 
     * @return {@code JSONType.STRING}
     */
    @Override
    public JSONType getType() {
        return JSONType.STRING;
    }

    /**
     * Attempts to convert this JSON string to a JSON object. Converting
     * from JSON string to JSON object is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONObject asObject() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON string to JSON object");
    }

    /**
     * Attempts to convert this JSON string to a JSON array. Converting
     * from JSON string to JSON array is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONArray asArray() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON string to JSON array");
    }

    /**
     * Attempts to convert this JSON string to a JSON boolean. If this
     * JSON string is {@code "true"}, then a {@code true} JSON boolean
     * is returned. Otherwise, a {@code false} JSON boolean is returned.
     * 
     * @return the parsed JSON boolean from this JSON string
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONBoolean asBoolean() throws JSONConversionException {
        if (getString().equals("true")) {
            return JSONBoolean.TRUE;
        } else {
            return JSONBoolean.FALSE;
        }
    }

    /**
     * Attempts to convert this JSON string to a JSON number. This is
     * achieved by parsing this string as a number, then returning the
     * result. The parsing is done using {@code Double.valueOf(String)}.
     * 
     * @return the parsed JSON number from this JSON string
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONNumber asNumber() throws JSONConversionException {
        return new JSONNumber(Double.valueOf(getString()));
    }

    /**
     * Attempts to convert this JSON number to a JSON string. Since this
     * is a JSON string, this JSON string is returned.
     * 
     * @return this JSON string
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONString asString() throws JSONConversionException {
        return this;
    }

    /**
     * Returns the string representation of this JSON text string. It is equivalent to:
     * <pre>
     * "\"" + getString() + "\""
     * </pre>
     * 
     * @param indentLevel this parameter has no effect
     * @param jsonFormat this parameter has no effect
     * @return the string representation of this JSON text string
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return "\"" + string + "\"";
    }

    public static JSONString of(String string) {
        return new JSONString(string);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    /**
     * Checks if this JSON string is equal to another object. A JSON string
     * is equal to an object if it is of type {@code JSONString} and its
     * string is equivalent to the string of this object. {@code getString()}
     * must equal {@code ((JSONString) obj).getString()}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JSONString) {
            return getString().equals(((JSONString) obj).getString());
        } else {
            return false;
        }
    }
}