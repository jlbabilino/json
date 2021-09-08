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

import com.jlbabilino.json.JSONEntry.JSONType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a type as being JSON serializable.
 * <code>JSONSerializer</code> checks for this annotation before serializing an
 * object.
 * 
 * @author Justin Babilino
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JSONSerializable {
    /**
     * The root type that the annotated object should be serialized to. This is
     * usually a JSON object because it aligns with the structure of most classes.
     * 
     * @return the root type
     */
    public JSONType rootType() default JSONType.OBJECT;
}