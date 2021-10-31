/*
 * Copyright (C) 2021 Triple Helix Robotics - FRC Team 2363
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

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public JSONType getType() {
        return JSONType.STRING;
    }

    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        return "\"" + string + "\"";
    }
}