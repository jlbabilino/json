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
 * This exception is thrown when a user attempts to parse a JSON file, but there
 * is an error.
 *
 * @author Justin Babilino
 */
public class JSONParserException extends Exception {

    /**
     * Constructs a <code>JSONNParserException</code> with a message to be
     * communicated to the user.
     *
     * @param index the index that the parsing error occurred
     * @param lineNumber the line number in the JSON where the error occurred
     * @param message the <code>String</code> message communicated to the user
     */
    public JSONParserException(int index, int lineNumber, String message) {
        super("Error at index " + index + ", on line " + lineNumber + ", " + message);
    }
}
