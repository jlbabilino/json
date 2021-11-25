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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a field value or method return value
 * should be serialized into JSON. It can only be used with Java classes that
 * are {@link JSONSerializable} for JSON arrays. {@code index()} indicates the
 * index where the serialized JSON should be placed in the new JSON array.
 * 
 * @see JSONSerializer
 * @author Justin Babilino
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SerializedJSONArrayItem {
    /**
     * Indicates where the serialized JSON should be placed in the new JSON array.
     * 
     * @return the index in the new JSON array
     */
    public int index();
}