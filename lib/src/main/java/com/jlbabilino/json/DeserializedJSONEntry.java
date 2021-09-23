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
 * entry data. For fields, make sure that they have {@code public} visibility.
 * The deserializer will convert the JSON entry being deserialized to the type
 * specified in the field or parameter, if it is able to. This annotation is
 * mainly used for two purposes:
 * <ol>
 * <li>For classes that are {@link JSONSerializable} for JSON numbers, booleans,
 * and strings. The JSON entries for these types can be converted to their
 * respective Java types and passed in some form to the newly created
 * object.
 * <li>For classes that prefer to manually deserialize the entire
 * {@link JSONEntry} being deserialized. This is particularly useful if the
 * amount of entries in a JSON object or the length of a JSON array is variable,
 * because {@link DeserializedJSONObjectValue} always uses the same keys, and
 * {@link DeserializedJSONArrayItem} always has data at the same indices. This
 * annotation bypasses that by allowing a class to manually sort through data.
 * </ol>
 * 
 * @author Justin Babilino
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface DeserializedJSONEntry {
}