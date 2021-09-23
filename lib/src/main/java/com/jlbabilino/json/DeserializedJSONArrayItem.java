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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field or parameter as a target for deserialized JSON
 * array data. For fields, make sure that they have {@code public} visibility.
 * For parameters of methods or constructors marked with
 * {@link DeserializedJSONTarget}, use this annotation on each parameter if the
 * class is {@link JSONSerializable} from JSON array types. The deserializer
 * will grab the JSON array item at the index specified in this annotation and
 * will assign it to the field or pass it as the parameter.
 * 
 * @see DeserializedJSONObjectValue
 * @author Justin Babilino
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface DeserializedJSONArrayItem {
    /**
     * @return the index in the JSON array to deserialize from
     */
    public int index();
}