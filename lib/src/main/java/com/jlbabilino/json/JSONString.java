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
    public JSONString(String string) {
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
     * Returns the string representation of this JSON text string. It is equivalent to:
     * <pre>
     * "\"" + getString() + "\""
     * </pre>
     * 
     * @param indentLevel this parameter has no effect on the result or effect of this implementation of this method
     * @param jsonFormat this parameter has no effect on the result or effect of this implementation of this method
     * @return the string representation of this JSON text string
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return "\"" + string + "\"";
    }
}