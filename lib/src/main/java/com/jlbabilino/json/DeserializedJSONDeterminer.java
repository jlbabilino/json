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
 * <p>
 * Marks a specific type of method in a {@code JSONSerializable} interface or
 * abstract class as the "determiner method." The determiner serves as a basic
 * way to achieve polymorphisim in JSON deserialization. When a
 * {@code JSONEntry} is deserialized to an interface or abstract class type, the
 * deserializer checks for a method with this annotation and these conditions:
 * <ul>
 * <li>The method is {@code static}
 * <li>The method has no type parameters.
 * <li>There is exactly one parameter of the appropriate {@link JSONEntry} type,
 * meaning that it matches the type of JSON that the interface or abstract class
 * is {@link JSONSerializable} to. For example, an interface that is
 * serializable for JSON objects would need this parameter to be of type
 * {@link JSONObject}.
 * <li>The return type is {@link TypeMarker}.
 * <li>The TypeMarker returned is parameterized, and it follows the format
 * {@code TypeMarker<? extends TYPE>}, where {@code TYPE} is this abstract class
 * or interface.
 * <li>If this interface or abstract class is generic, the wildcard inside the
 * type marker should extend the interface or abstract class, but it should also
 * be parameterized. The parameters can be of any type, but most of the time
 * they will all be wildcards.
 * <li>The method {@code throws} {@code JSONDeserializationException}
 * <li>Although it is not checked for, the determiner should not return a
 * TypeMarker that represents the interface or abstract class itselft, as that
 * will cause infinite recursion.
 * </ul>
 * <p>
 * The determiner reads the contents of the {@code JSONEntry} and determines the
 * appropriate type that this {@code JSONEntry} should be deserialized to, based
 * solely on the {@code JSONEntry}, and returns it as a {@code TypeMarker}
 * object. Since {@code TypeMarker} objects are not dynamic and must be created
 * at compile-time, it is good practice to create all possible type markers as
 * {@code private static final} fields containing anonymous inner classes in the
 * interface or abstract class. These can be selected from when the method is
 * invoked. The determiner cannot always make the appropriate choice between
 * these types, as sometimes the {@code JSONEntry} is not in the correct format.
 * When this occurs, the method can throw a {@code JSONDeserializationException}
 * with a message that will be propogated back to the class that invoked the
 * deserialization method.
 * <p>
 * The {@code JSONEntry} passed into this method by the deserializer will never
 * be {@code null}.
 * 
 * @author Justin Babilino
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeserializedJSONDeterminer {
}
