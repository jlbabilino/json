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
    public JSONNumber(Number number) {
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
}
