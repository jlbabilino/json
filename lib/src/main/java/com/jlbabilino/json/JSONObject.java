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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a JSON object type. It contains key-value pairs where
 * the key is a {@code JSONString}, and the value is a {@code JSONEntry}. It
 * uses a Java {@code Map} to achieve this.
 * 
 * @author Justin Babilino
 */
public class JSONObject extends JSONEntry {

    /**
     * The {@code Map} that stores key-value pairs for JSON objects
     */
    private final Map<JSONString, JSONEntry> map;

    /**
     * Constructs a {@code JSONObject} with a {@code Map} containing
     * {@link JSONEntry} objects paired with {@code JSONString} keys.
     *
     * @param map key-value pairs of {@code JSONString} and {@code JSONEntry}
     */
    JSONObject(Map<JSONString, JSONEntry> map) {
        this.map = map;
    }

    /**
     * Returns the underlying map of this JSON object. This method is
     * package-private because returning this map would allow the user to modify
     * the map, even though this type is meant to be immutable.
     * 
     * @return the map of this JSON object
     */
    Map<JSONString, JSONEntry> getMap() {
        return map;
    }

    /**
     * Retrieves the mapped {@link JSONEntry} from the given {@code JSONString} key, if
     * it is availible. A key is availible if and only if {@code containsKey()}
     * returns {@code true}.
     *
     * @param key the {@code JSONString} key used to retrieve the value
     * @return the {@code JSONEntry} associated with the key provided
     * @throws JSONKeyNotFoundException if the key does not exist in the object
     */
    public JSONEntry get(JSONString key) throws JSONKeyNotFoundException {
        if (containsKey(key)) {
            return map.get(key);
        } else {
            throw new JSONKeyNotFoundException("Key \"" + key + "\" not found in this JSON object.");
        }
    }

    /**
     * Retrieves the mapped {@link JSONEntry} from the given {@code String} key, if
     * it is availible. A key is availible if and only if {@code containsKey()}
     * returns {@code true}.
     *
     * @param key the {@code String} key used to retrieve the value
     * @return the {@code JSONEntry} associated with the key provided
     * @throws JSONKeyNotFoundException if the key does not exist in the object
     */
    public JSONEntry get(String key) throws JSONKeyNotFoundException {
        return get(new JSONString(key));
    }

    /**
     * Checks if there is a mapping for this JSON string in this JSON object.
     * 
     * @param key the key to check for
     * @return {@code true} if and only if the key exists in this JSON object
     */
    public boolean containsKey(JSONString key) {
        return map.containsKey(key);
    }

    /**
     * Checks if there is a mapping for this string in this JSON object.
     * 
     * @param key the key to check for
     * @return {@code true} if and only if the key exists in this JSON object
     */
    public boolean containsKey(String key) {
        return containsKey(new JSONString(key));
    }

    /**
     * Returns the number of key-value mappings in this JSON object.
     * 
     * @return the number of key-value mappings in this JSON object
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns {@code true} if and only if there are no key-value mappings in this
     * JSON object. The return value of this method is identical to
     * {@code size() == 0}.
     * 
     * @return {@code true} if and only if there are no key-value mappings in this
     *         JSON object
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns {@code true} since this entry is an object.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isObject() {
        return true;
    }

    /**
     * Returns {@code JSONType.OBJECT} since this entry is an object.
     * 
     * @return {@code JSONType.OBJECT}
     */
    @Override
    public JSONType getType() {
        return JSONType.OBJECT;
    }

    /**
     * Attempts to convert this JSON object to a JSON object. Since this
     * is a JSON object, this JSON object is returned.
     * 
     * @return this JSON object
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONObject asObject() throws JSONConversionException {
        return this;
    }

    /**
     * Attempts to convert this JSON object to a JSON array. Conversion from
     * JSON object to JSON array is defined by extracting each key-value pair in
     * the JSON object as a new JSON object and placing each of these in an array
     * in no particular order.
     * 
     * @return this JSON object as a JSON array
     * @throws JSONConversionException never since this conversion is supported
     */
    @Override
    public JSONArray asArray() throws JSONConversionException {
        JSONEntry[] jsonEntries = new JSONEntry[size()];
        int i = 0;
        for (Map.Entry<JSONString, JSONEntry> entry : map.entrySet()) {
            jsonEntries[i] = new JSONObject(Map.of(entry.getKey(), entry.getValue()));
            i++;
        }
        return new JSONArray(jsonEntries);
    }

    /**
     * Attempts to convert this JSON object to a JSON boolean. Converting
     * from JSON object to JSON boolean is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONBoolean asBoolean() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON object to JSON boolean");
    }

    /**
     * Attempts to convert this JSON object to a JSON number. Converting
     * from JSON object to JSON number is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONNumber asNumber() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON object to JSON number");
    }

    /**
     * Attempts to convert this JSON object to a JSON string. Converting
     * from JSON object to JSON string is not supported, so this method
     * will always result in a {@code JSONConversionException}.
     * 
     * @return nothing since this conversion is not supported
     * @throws JSONConversionException always since this conversion is not supported
     */
    @Override
    public JSONString asString() throws JSONConversionException {
        throw new JSONConversionException("Cannot convert JSON object to JSON string");
    }

    /**
     * Generates a {@code String} that represents this JSON object. Creates curly
     * braces around the object, and places all key-value pairs in random order, separated by
     * commas. For example, some JSON object could be represented as
     * <p>
     * "{"entry": [9, 10], "hi": null, "json": true}"
     * <p>
     * Format options are availible for this method. There are three options that
     * will affect the result of this method:
     * <ol>
     * <li>{@code indentLevel}: This tells the method which indent level the object
     * is beginning on. For example, if this were the root JSON entry, then the
     * indent level would be {@code 0}. If this object were a value associated with a
     * key in a JSON object that were the root of the JSON, then the indent level
     * would be {@code 1} because it is one level inside the JSON.
     * <li>{@code indentSpaces}
     * <li>{@code objectNewlinePerItem}
     * <li>{@code objectBeginOnNewline}
     * </ol>
     * 
     * @param indentLevel the indent level of the object declaration ({)
     * @param jsonFormat formatting options
     * @return the string representation of this JSON object
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        if (map.isEmpty()) {
            return "{}"; // if nothing in map just spit this out
        }

        String indentBlock = JSONFormat.getIndentString(jsonFormat);

        StringBuilder shortIndentBlock = new StringBuilder(); // this block is for the indent level that the object
                                                              // itself is on
        for (int i = 0; i < indentLevel; i++) {
            shortIndentBlock.append(indentBlock);
        }
        String longIndentBlock = shortIndentBlock.toString() + indentBlock.toString(); // this block is for the indent
                                                                                       // level that the key-value pairs
                                                                                       // are on

        StringBuilder str = new StringBuilder();
        if (JSONFormat.objectBeginOnNewline(jsonFormat) && indentLevel != 0) { // if this is the root entry (indent level is
                                                                          // 0) then don't enter down no matter what
            str.append(System.lineSeparator()).append(shortIndentBlock); // if the object should begin on a new line,
                                                                         // add a newline and short indent
        }

        str.append("{"); // add the initial open brace {

        String strBetweenItems;
        if (JSONFormat.objectNewlinePerItem(jsonFormat)) {
            strBetweenItems = System.lineSeparator() + longIndentBlock; // if there should be newlines, insert them and
                                                                        // indent
            str.append(strBetweenItems);
        } else {
            strBetweenItems = " ";
        }

        // Iterator<Map.Entry<JSONString, JSONEntry>> setIterator = map.entrySet().iterator();
        Iterator<Map.Entry<JSONString, JSONEntry>> setIterator = map.entrySet().stream().sorted(
                (a, b) -> a.getKey().getString().compareTo(b.getKey().getString())).iterator();

        Map.Entry<JSONString, JSONEntry> firstEntry = setIterator.next();
        str.append(firstEntry.getKey()).append(": ") // add the first key
                .append(firstEntry.getValue().toJSONText(indentLevel + 1, jsonFormat)); // add the first value

        setIterator.forEachRemaining(entry -> { // I use a lambda to avoid having to assign the next item over and over
                                                // again
            str.append(",").append(strBetweenItems) // add comma and newline
                    .append("\"").append(entry.getKey().getString()).append("\": ") // add the key and colon :
                    .append(entry.getValue().toJSONText(indentLevel + 1, jsonFormat)); // add the value
        });

        if (JSONFormat.objectNewlinePerItem(jsonFormat)) {
            str.append(System.lineSeparator()).append(shortIndentBlock);
        }

        str.append("}"); // add final newline and close brace }

        return str.toString();
    }

    /**
     * Creates a {@code JSONObject} given a {@code Map} of key-value pairs.
     * 
     * @param jsonObjectMap the map from {@code JSONString} keys to {@code JSONEntry} values
     * @return a {@code JSONObject} wrapping a copy of the map
     * @throws NullPointerException if the map or a value in it is {@code null}
     */
    public static JSONObject of(Map<JSONString, JSONEntry> jsonObjectMap) throws NullPointerException {
        if (jsonObjectMap == null) {
            throw new NullPointerException("Cannot instantiate a JSONObject with a null map of entries.");
        }
        for (Map.Entry<JSONString, JSONEntry> entry : jsonObjectMap.entrySet()) {
            if (entry.getValue() == null) {
                throw new NullPointerException("Cannot instantiate a JSONObject with a null value in the object map.");
            }
        }
        return new JSONObject(new HashMap<>(jsonObjectMap));
    }

    @Override
    public int hashCode() {
        return getMap().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JSONObject) {
            return getMap().equals(((JSONObject) obj).getMap());
        } else {
            return false;
        }
    }
}
