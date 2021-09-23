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

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public JSONType getType() {
        return JSONType.NUMBER;
    }

    @Override
    public String getJSONText(int indentLevel, JSONFormat format) {
        return number.toString();
    }
}
