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

import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a JSON object type. It contains key-value pairs where
 * the key is a {@code String}, and the value is a {@code JSONEntry}. It
 * implements a <code>Map</code> to achieve this. If a program creates JSON data
 * manually (rather than from serialization), then the map used to construct the
 * {@code JSONObject} will not be exposed to other classes directly in any way,
 * and it will not modify the map, but it will access methods in it. This means
 * that modifications to the map WILL AFFECT this object. Please try to keep the
 * map used for construction hidden from other classes after it is instantiated
 * with the appropriate data.
 * 
 * @author Justin Babilino
 */
public class JSONObject extends JSONEntry {

    /**
     * The {@code Map} that stores key-value pairs for JSON objects
     */
    private final Map<String, JSONEntry> map;

    /**
     * Constructs a {@code JSONObject} with a {@code Map} containing
     * {@link JSONEntry} objects paired with string keys.
     *
     * @param map key-value pairs of <code>String</code> and <code>JSONEntry</code>
     */
    public JSONObject(Map<String, JSONEntry> map) {
        this.map = map;
    }

    /**
     * Returns the underlying map of this JSON object. This method is
     * package-private because returning this map would allow the user to modify
     * the map even though this type is meant to be immutable.
     * 
     * @return the map of this JSON object
     */
    Map<String, JSONEntry> getMap() {
        return map;
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
        if (containsKey(key)) {
            return map.get(key);
        } else {
            throw new JSONKeyNotFoundException("Key \"" + key + "\" not found in this JSON object.");
        }
    }

    /**
     * Checks if a string key contains a mapping in this JSON object.
     * 
     * @param key the key to check for
     * @return <code>true</code> if and only if the key exists in this object
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
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

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public JSONType getType() {
        return JSONType.OBJECT;
    }

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
        if (JSONFormat.getObjectBeginOnNewline(jsonFormat) && indentLevel != 0) { // if this is the root entry (indent level is
                                                                          // 0) then don't enter down no matter what
            str.append(System.lineSeparator()).append(shortIndentBlock); // if the object should begin on a new line,
                                                                         // add a newline and short indent
        }

        str.append("{"); // add the initial open brace {

        String strBetweenItems;
        if (JSONFormat.getObjectNewlinePerItem(jsonFormat)) {
            strBetweenItems = System.lineSeparator() + longIndentBlock; // if there should be newlines, insert them and
                                                                        // indent
            str.append(strBetweenItems);
        } else {
            strBetweenItems = " ";
        }

        Iterator<Map.Entry<String, JSONEntry>> setIterator = map.entrySet().iterator();

        Map.Entry<String, JSONEntry> firstEntry = setIterator.next();
        str.append("\"").append(firstEntry.getKey()).append("\": ") // add the first key
                .append(firstEntry.getValue().toJSONText(indentLevel + 1, jsonFormat)); // add the first value

        setIterator.forEachRemaining(entry -> { // I use a lambda to avoid having to assign the next item over and over
                                                // again
            str.append(",").append(strBetweenItems) // add comma and newline
                    .append("\"").append(entry.getKey()).append("\": ") // add the key and colon :
                    .append(entry.getValue().toJSONText(indentLevel + 1, jsonFormat)); // add the value
        });

        if (JSONFormat.getObjectNewlinePerItem(jsonFormat)) {
            str.append(System.lineSeparator()).append(shortIndentBlock);
        }

        str.append("}"); // add final newline and close brace }

        return str.toString();
    }
}
