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
 * <p>
 * This abstract class is the super class of every type of entry possible in a
 * JSON structure. Each entry class that contains sub-entries always uses this
 * type for those entries.
 * 
 * <p>
 * Here's an example: This is an example JSON file that could be imported into
 * this system: <code>
 * <br>{
 * <br>&nbsp;&nbsp;&nbsp; "people": [{
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "name": "Justin",
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "age": 16
 * <br>&nbsp;&nbsp;&nbsp; }, {
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "name": "Joe",
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "age": 23
 * <br>&nbsp;&nbsp;&nbsp; }]
 * <br>}
 * </code>
 * 
 * <p>
 * Let's say that the root object of this has been imported into a variable
 * {@code entry}.
 * 
 * <p>
 * Retrieving the name of the first person:
 * 
 * <p>
 * 
 * <pre>
 * JSONObject rootObject = (JSONObject) entry;
 * JSONArray peopleArray = (JSONArray) rootObject.get("people");
 * JSONObject firstPersonObject = (JSONObject) peopleArray.get(0);
 * JSONString nameString = (JSONString) firstPersonObject.get("name");
 * String firstPersonName = nameString.getString();
 * </pre>
 * 
 * <p>
 * Let's break this down:
 * 
 * <p>
 * 
 * <pre>
 * JSONObject rootObject = (JSONObject) entry;
 * </pre>
 * 
 * <p>
 * This casts the root {@code JSONEntry} to a {@code JSONObject} so that
 * key-value pairs can be accessed later.
 * 
 * <p>
 * 
 * <pre>
 * JSONArray peopleArray = (JSONArray) rootObject.get("people");
 * </pre>
 * 
 * <p>
 * This gets the {@code JSONEntry} mapped to the key {@code "people"} and casts
 * it as a {@code JSONArray} so that items can be accessed later.
 * 
 * <p>
 * 
 * <pre>
 * JSONObject firstPersonObject = (JSONObject) peopleArray.get(0);
 * </pre>
 * 
 * <p>
 * This gets the first {@code JSONEntry} in the array and casts it as a
 * {@code JSONObject} because that is the type of the array item.
 * 
 * <p>
 * 
 * <pre>
 * JSONString nameString = (JSONString) firstPersonObject.get("name");
 * </pre>
 *
 * <p>
 * This gets the value mapped to the key {@code "name"} and casts it as a
 * {@code JSONString}, since the value is a JSON string.
 * 
 * <p>
 * 
 * <pre>
 * String firstPersonName = nameString.getString();
 * </pre>
 * 
 * <p>
 * This gets the name of the first person in the array of people, as a
 * {@code String}, through the {@code getString()} method of {@link JSONString}.
 * 
 * <p>
 * Data can be extracted through this method, but it is tedious and slightly
 * unintuitive, so it is recomended that data is deserialized through
 * {@link JSONDeserializer} to convert JSON data to Java data.
 * 
 * @author Justin Babilino
 */
public abstract class JSONEntry {

    public static enum JSONType {
        OBJECT, ARRAY, BOOLEAN, NUMBER, STRING, NULL
    }

    /**
     * Returns {@code true} if this entry is an object, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isObject() {
        return false;
    }

    /**
     * Returns {@code true} if this entry is an array, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Returns {@code true} if this entry is a boolean, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * Returns {@code true} if this entry is a number, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * Returns {@code true} if this entry is a string, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isString() {
        return false;
    }

    /**
     * Returns {@code true} if this entry is a null, {@code false} otherwise.
     *
     * @return type check as {@code boolean}
     */
    public boolean isNull() {
        return false;
    }

    /**
     * Returns the type of entry this JSON entry is.
     * 
     * @return type of JSON entry
     */
    public abstract JSONType getType();

    /**
     * Generates a {@code String} that represents this JSON entry as properly spaced
     * and indented JSON text that can be saved to a file.
     *
     * @param indentLevel the amount of levels of indent; used to determine how many
     *                    spaces to place before text on newlines
     * @param format      formatting data used when generating text
     * @return a {@code String} representing this JSON entry
     */
    public abstract String getJSONText(int indentLevel, JSONFormat format);

    /**
     * Package-private method that gives the {@link Class} in this library that
     * represents a certain {@link JSONType}. For example:
     * 
     * <pre>
     * classForJSONType(JSONType.OBJECT)
     * </pre>
     * 
     * returns
     * 
     * <pre>
     * JSONObject.class
     * </pre>
     * 
     * @param the {@code JSONType}
     * @return the associated {@code Class}
     */
    static Class<? extends JSONEntry> classForJSONType(JSONType jsonType) {
        switch (jsonType) {
        case OBJECT:
            return JSONObject.class;
        case ARRAY:
            return JSONArray.class;
        case BOOLEAN:
            return JSONBoolean.class;
        case NUMBER:
            return JSONNumber.class;
        case STRING:
            return JSONString.class;
        case NULL:
            return JSONNull.class;
        default:
            return null;
        }
    }

    /**
     * Returns a string representation of this {@code JSONEntry}, using the default
     * format options. This is identical to:
     * 
     * <pre>
     * getJSONText(0, new JSONFormat())
     * </pre>
     *
     * @return this {@code JSONEntry} as a String
     */
    @Override
    public String toString() {
        return getJSONText(0, new JSONFormat());
    }
}