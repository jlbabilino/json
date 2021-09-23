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

/**
 * This class models all formatting options for JSON exporting. It allows for
 * the adjustment of indent spaces and array and object newline settings.
 *
 * @author Justin Babilino
 */
public class JSONFormat {

    // These options are set with default settings that can be overwritten later
    private JSONFormatOption.IndentSpaces indentSpaces = JSONFormatOption.IndentSpaces.FOUR;
    private JSONFormatOption.ArrayNewlinePerItem arrayNewlinePerItem = JSONFormatOption.ArrayNewlinePerItem.TRUE;
    private JSONFormatOption.ArrayBeginOnNewline arrayBeginOnNewline = JSONFormatOption.ArrayBeginOnNewline.FALSE;
    private JSONFormatOption.ObjectNewlinePerItem objectNewlinePerItem = JSONFormatOption.ObjectNewlinePerItem.TRUE;
    private JSONFormatOption.ObjectBeginOnNewline objectBeginOnNewline = JSONFormatOption.ObjectBeginOnNewline.FALSE;

    /**
     * Constructs a <code>JSONFormat</code> with an array of format options. Default
     * options are selected if they are not given in the options array. Null inputs
     * will not affect the format, and duplicate inputs will result in the input
     * with the greatest index taking affect.
     *
     * @param options an array of formatting options
     */
    public JSONFormat(JSONFormatOption... options) {
        for (JSONFormatOption option : options) {
            if (option instanceof JSONFormatOption.IndentSpaces) {
                indentSpaces = (JSONFormatOption.IndentSpaces) option;
            } else if (option instanceof JSONFormatOption.ArrayNewlinePerItem) {
                arrayNewlinePerItem = (JSONFormatOption.ArrayNewlinePerItem) option;
            } else if (option instanceof JSONFormatOption.ArrayBeginOnNewline) {
                arrayBeginOnNewline = (JSONFormatOption.ArrayBeginOnNewline) option;
            } else if (option instanceof JSONFormatOption.ObjectNewlinePerItem) {
                objectNewlinePerItem = (JSONFormatOption.ObjectNewlinePerItem) option;
            } else if (option instanceof JSONFormatOption.ObjectBeginOnNewline) {
                objectBeginOnNewline = (JSONFormatOption.ObjectBeginOnNewline) option;
            }
        }
    }

    /**
     * @return the options for indent spaces
     */
    public JSONFormatOption.IndentSpaces getIndentSpaces() {
        return indentSpaces;
    }

    /**
     * @return the options for array newlines
     */
    public JSONFormatOption.ArrayNewlinePerItem getArrayNewlinePerItem() {
        return arrayNewlinePerItem;
    }

    /**
     * @return the options for array initial newlines
     */
    public JSONFormatOption.ArrayBeginOnNewline getArrayBeginOnNewline() {
        return arrayBeginOnNewline;
    }

    /**
     * @return the options for object newlines
     */
    public JSONFormatOption.ObjectNewlinePerItem getObjectNewlinePerItem() {
        return objectNewlinePerItem;
    }

    /**
     * @return the options for object initial newlines
     */
    public JSONFormatOption.ObjectBeginOnNewline getObjectBeginOnNewline() {
        return objectBeginOnNewline;
    }
}
