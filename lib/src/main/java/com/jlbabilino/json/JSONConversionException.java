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
 * This exception is thrown when the user attempts to convert a JSON entry
 * of one type to a JSON entry of another type, and the conversion was not
 * possible.
 * 
 * @author Justin Babilino
 */
public class JSONConversionException extends RuntimeException {
    
    /**
     * Constructs a {@code JSONConversionException} with a string message.
     * 
     * @param message the message communicated to the user
     */
    public JSONConversionException(String message) {
        super(message);
    }
}
