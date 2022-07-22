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
 * This exception is thrown by {@link JSONSerializer} if there is an error
 * while serializing. It will provide a detailed message describing what the
 * problem is and how to fix it.
 * 
 * @see JSONSerializer
 * @author Justin Babilino
 */
public class JSONSerializerException extends Exception {
    /**
     * Creates a {@code JSONSerializerException} with a string error message.
     * 
     * @param message the error message
     */
    public JSONSerializerException(String message) {
        super(message);
    }
}