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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This {@code JSONEntry} holds an array of {@code JSONEntry} types. It is
 * equivalent to the square bracket [a, b, c,...] array structure in a JSON.
 * JSON arrays can hold strings, numbers, booleans, objects, and other arrays.
 * These arrays do not check for types, meaning that multiple types can be in
 * the same array simultaneously, unlike Java arrays which must have values of
 * the same types.
 *
 * @see JSONEntry
 * @see JSONObject
 * @author Justin Babilino
 */
public class JSONArray extends JSONEntry implements Iterable<JSONEntry> {

    /**
     * The array of entries in the array
     */
    private final JSONEntry[] array;

    /**
     * Constructs an <code>ArrayJSONEntry</code> with an array of entries.
     *
     * @param array array of <code>JSONEntries</code>
     */
    public JSONArray(JSONEntry[] array) {
        this.array = array;
    }

    /**
     * Returns the array of JSON entries in this JSON array. This method is
     * package-private because returning this array would allow the user to modify
     * the array even though this type is meant to be immutable.
     * 
     * @return the array of JSON entries
     */
    JSONEntry[] getArray() {
        return array;
    }

    /**
     * Retrieves the {@link JSONEntry} at the index provided, if the index is in
     * bounds for the {@code JSONArray}.
     * 
     * @param index the index to retrieve the {@code JSONEntry} from in the array
     * @return the {@code JSONEntry} at the index provided in the array
     * @throws ArrayIndexOutOfBoundsException if the provided index is out of bounds
     */
    public JSONEntry get(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= array.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Index " + index + " out of bounds for JSON array of length " + array.length + ".");
        }
        return array[index];
    }

    /**
     * Returns the length of this JSON array.
     * 
     * @return the length of this JSON array
     */
    public int length() {
        return array.length;
    }

    /**
     * Returns a new {@link Iterator} that allows this JSON array to be iterated on.
     * Each call to this method returns a new iterator instance.
     * 
     * @return the new iterator
     */
    @Override
    public Iterator<JSONEntry> iterator() {
        return new JSONArrayIterator();
    }

    /**
     * This member class is used by the {@code iterator()} method to generate
     * iterators.
     */
    private class JSONArrayIterator implements Iterator<JSONEntry> {

        /**
         * The current index of this iterator
         */
        private int index;

        /**
         * Constructs a {@code JSONArrayIterator} for iteration over JSON arrays.
         */
        private JSONArrayIterator() {
            index = 0;
        }

        /**
         * Returns {@code true} if there is another JSON entry availible in this iterator, {@code false} if no more are availible.
         * 
         * @return {@code true} if there is another JSON entry availible in this iterator, {@code false} if no more are availible
         */
        @Override
        public boolean hasNext() {
            return index < array.length - 1;
        }

        /**
         * Retrieves the next JSON entry in this iterator. Throws an exception if {@code hasNext()} returns false.
         * 
         * @return the next JSON entry
         * @throws NoSuchElementException if there are no more availible items in this iterator
         */
        @Override
        public JSONEntry next() throws NoSuchElementException {
            if (hasNext()) {
                return array[++index]; // pretty neat huh?
            } else {
                throw new NoSuchElementException("No more JSONEntry elements availible in the array.");
            }
        }
    }

    /**
     * Returns {@code true} since this entry is an array.
     * 
     * @return {@code true}
     */
    @Override
    public boolean isArray() {
        return true;
    }

    /**
     * Returns {@code JSONType.ARRAY} since this entry is an array.
     * 
     * @return {@code JSONType.ARRAY}
     */
    @Override
    public JSONType getType() {
        return JSONType.ARRAY;
    }

    /**
     * Generates a {@code String} that represents this JSON array. Creates square
     * brackets around the array, and places all values in order, separated by
     * commas. For example, some JSON array could be represented as
     * <p>
     * "[1.4, null, "stringy", {"entry": 10}]"
     * <p>
     * Format options are availible for this method. There are three options that
     * will affect the result of this method:
     * <ol>
     * <li>{@code indentLevel}: This tells the method which indent level the array
     * is beginning on. For example, if this were the root JSON entry, then the
     * indent level would be {@code 0}. If this array was a value associated with a
     * key in a JSON object that was the root of the JSON, then the indent level
     * would be {@code 1} because it is one level inside the JSON.
     * <li>{@code indentSpaces}
     * <li>{@code arrayNewlinePerItem}
     * <li>{@code arrayBeginOnNewline}
     * </ol>
     * 
     * @param indentLevel the indent level of the array declaration ([)
     * @param jsonFormat formatting options
     * @return the string representation of this JSON array
     */
    @Override
    public String toJSONText(int indentLevel, int jsonFormat) {
        if (array.length == 0) {
            return "[]"; // if nothing in array just spit this out
        }

        String indentBlock = JSONFormat.getIndentString(jsonFormat);
        StringBuilder shortIndentBlock = new StringBuilder(); // this block is for the indent level that the array
                                                              // itself is on
        for (int i = 0; i < indentLevel; i++) {
            shortIndentBlock.append(indentBlock);
        }
        String longIndentBlock = shortIndentBlock.toString() + indentBlock.toString(); // this block is for the indent
                                                                                       // level that the array items are
                                                                                       // on

        StringBuilder str = new StringBuilder(); // the JSON text string we are building

        if (JSONFormat.arrayBeginOnNewline(jsonFormat) && indentLevel != 0) { // if this is the root entry (indent level is
                                                                         // 0) then don't enter down no matter what
            str.append(System.lineSeparator()).append(shortIndentBlock); // if the array should begin on a new line, add
                                                                         // a newline and short indent
        }
        str.append("["); // begin array

        String strBetweenItems; // this string goes after the comma and before the next item in arrays. It
                                // changes depending on the format
        if (JSONFormat.arrayNewlinePerItem(jsonFormat)) {
            strBetweenItems = System.lineSeparator() + longIndentBlock; // if there should be newlines between items,
                                                                        // modify strBetweenItms
            str.append(strBetweenItems);
        } else {
            strBetweenItems = " "; // we don't append anything here so there isn't a random space between the open
                                   // bracket and first item (like this: [ "test", 1])
        }

        str.append(array[0].toJSONText(indentLevel + 1, jsonFormat)); // add the first item
        for (int i = 1; i < array.length; i++) {
            str.append(",").append(strBetweenItems).append(array[i].toJSONText(indentLevel + 1, jsonFormat)); // add comma,
                                                                                                           // strBetweenItems,
                                                                                                           // and next
                                                                                                           // item
        }

        if (JSONFormat.arrayNewlinePerItem(jsonFormat)) {
            str.append(System.lineSeparator()).append(shortIndentBlock); // only place close bracket ] on newline if
                                                                         // each item has been on a new line
        }
        str.append("]"); // add final newline and close bracket ]

        return str.toString();
    }
}