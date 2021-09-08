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
 * This exception is thrown when a user attempts to retrieve a keyed entry from
 * a JSON object that does not contain an entry with that key. For example, this
 * would be thrown if a user tried to get a named entry called "age" for a
 * "person" when there was no entry in the object with that name.
 * <p>
 * This is an <i>unchecked exception</i> so ensure that code that implements
 * this API knows what keys should be availible in each JSON object.
 *
 * @author Justin Babilino
 */
public class JSONKeyNotFoundException extends RuntimeException {

    /**
     * Constructs a <code>JSONNameNotFoundException</code> with a message to be
     * communicated to the user.
     *
     * @param message the <code>String</code> message communicated to the user.
     */
    public JSONKeyNotFoundException(String message) {
        super(message);
    }
}
