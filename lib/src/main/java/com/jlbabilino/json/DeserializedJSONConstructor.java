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
 * This annotation marks a <i>single</i> constructor in a
 * {@link JSONSerializable} class as the target for JSON deserialization. The
 * deserializer will invoke the constructor that has this annotation to create
 * the deserialized object. {@link JSONDeserializer} can pass parameters derived
 * from JSON into the constructor, allowing for immutable type support. For
 * example, a theoretical class called {@code ObjectHolder} could only have one
 * final field, and it could be initialized through the constructor only.
 * <p>
 * If multiple constructors are marked with this annotation, a random one will
 * be selected. Please make sure this does not happen as your program will have
 * an unpredictable output.
 * <p>
 * This annotation may also be availible in the future for use with "constructor
 * methods," which are static methods that return instances of the class. For
 * now, these are still unsupported.
 * 
 * @see DeserializedJSONTarget
 * @author Justin Babilino
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface DeserializedJSONConstructor {
}