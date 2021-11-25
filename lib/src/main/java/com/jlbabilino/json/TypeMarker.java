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
 * This interface is used to hold type information at runtime. The user is
 * intended to create anonymous inner instances of this class to be passed into
 * the deserializer.
 * 
 * <p>
 * When deserializing JSON, it is essential to know the Java type to deserialize
 * to, and it originally may appear that a Class object is sufficient. This
 * fails when deserializing generics, which is why {@code TypeMarker} exists.
 * </p>
 * <p>
 * This method of holding type information at runtime is basically a hack that
 * gets around type erasure. When an anonymous inner class is created off of
 * this interface, Java automatically converts it to a nested static class at
 * compile-time. This class can be introspected with Java reflection, which can
 * look at the superinterfaces of this inner class and can get the generic type
 * parameters of the {@code TypeMarker}. Here's an example of a private, static,
 * final member of class {@code TypeMarkerExample} of type {@code TypeMarker}
 * called {@code marker} that holds {@code Map&lt;String, Integer&gt;}:
 * </p>
 * 
 * <pre>
 * public class TypeMarkerExample {
 *     private static final TypeMarker&lt;Map&lt;String, Integer&gt;&gt; marker = new TypeMarker&lt;&gt;() {
 *     };
 * }
 * </pre>
 * <p>
 * This gets converted to:
 * </p>
 * 
 * <pre>
 * public class TypeMarkerExample {
 *     private static class TypeMarkerExample$0 implements TypeMarker&lt;Map&lt;String, Integer&gt;&gt; {
 *     }
 * 
 *     private static final TypeMarker&lt;Map&lt;String, Integer&gt;&gt; marker = new TypeMarkerExample$0();
 * }
 * </pre>
 * <p>
 * The deserializer then looks at the type parameter in the {@code TypeMarker}
 * after the {@code implements} statement, and determines the type.
 * </p>
 * 
 * @see JSONDeserializer
 */
public interface TypeMarker<T> {
}