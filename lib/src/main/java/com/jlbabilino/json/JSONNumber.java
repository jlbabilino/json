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
 * This class wraps a <code>Number</code> value in a <code>JSONEntry</code>.
 *
 * @author Justin Babilino
 */
public class JSONNumber extends JSONEntry {

    /**
     * The <code>Number</code> value in the entry.
     */
    private final Number number;

    /**
     * Constructs a <code>NumberJSONEntry</code> with a <code>Number</code>
     * value to be stored in the entry.
     *
     * @param number the <code>Number</code> value to be stored in this entry
     */
    JSONNumber(Number number) {
        this.number = number;
    }

    /**
     * Returns the numeric value of this JSON number.
     * 
     * @return the numeric value of this JSON number
     */
    public Number getNumber() {
        return number;
    }

    /**
     * Returns {@code true} since this entry is a number.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isNumber() {
        return true;
    }

    /**
     * Returns {@code JSONType.NUMBER} since this entry is a number.
     * 
     * @return {@code JSONType.NUMBER}
     */
    @Override
    public JSONType getType() {
        return JSONType.NUMBER;
    }

    /**
     * Attempts to convert this JSON number to a JSON object. Converting
     * from JSON number to JSON object is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONObject asObject() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON number to JSON object");
    }

    /**
     * Attempts to convert this JSON number to a JSON array. Converting
     * from JSON number to JSON array is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONArray asArray() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON number to JSON array");
    }

    /**
     * Attempts to convert this JSON number to a JSON boolean. The value of
     * {@code getNumber().equals(0)} is wrapped in a JSON boolean and returned.
     * 
     * @return the boolean representation of this JSON number
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONBoolean asBoolean() throws JSONConversionException {
        return new JSONBoolean(getNumber().equals(0));
    }

    /**
     * Attempts to convert this JSON number to a JSON number. Since this
     * is a JSON number, this JSON number is returned. 
     * 
     * @return this JSON number
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONNumber asNumber() throws JSONConversionException {
        return this;
    }

    /**
     * Attempts to convert this JSON number to a JSON string. Conversion to
     * string is achieved using {@code getNumber().toString()}
     * by passing in {@code getNumber()} and wrapping the value in a
     * {@code JSONString}.
     * 
     * @return the string representation of this JSON number
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONString asString() throws JSONConversionException {
        return new JSONString(getNumber().toString());
    }

    /**
     * Returns the JSON string representation of this number, it is equivalent to:
     * <pre>
     * getNumber().toString()
     * </pre>
     * 
     * @param indentLevel this parameter has no effect on the result or effect of this implementation of this method
     * @param jsonFormat this parameter has no effect on the result or effect of this implementation of this method
     * @return the number in JSON text form
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return number.toString();
    }

    /**
     * Creates a {@code JSONNumber} given a {@code Number}.
     * 
     * @param number the number
     * @return a {@code JSONNumber} wrapping the number
     * @throws NullPointerException if the number is {@code null}
     */
    public static JSONNumber of(Number number) throws NullPointerException {
        if (number == null) {
            throw new NullPointerException("Cannot instantiate a JSONNumber with a null number.");
        }
        return new JSONNumber(number);
    }

    /**
     * Checks if this JSON number is equal to another object. A JSON number
     * is equal to an object if it is of type {@code JSONNumber} and its
     * number is equivalent to the number of this object. {@code getNumber()}
     * must equal {@code ((JSONNumber) obj).getNumber()}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JSONNumber) {
            return getNumber().equals(((JSONNumber) obj).getNumber());
        } else {
            return false;
        }
    }
}
