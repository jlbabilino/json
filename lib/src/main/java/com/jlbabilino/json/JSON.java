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
 * This class wraps a JSON file into an object. It uses a <code>JSONEntry</code>
 * as the root and branches are built onto the root.
 *
 * @see JSONEntry
 * @author Justin Babilino
 */
public class JSON {

    /**
     * The root <code>JSONEntry</code> of the JSON--the first JSON structure in it
     */
    private final JSONEntry rootEntry;

    /**
     * Constructs a <code>JSON</code> with the root JSON entry.
     *
     * @param rootEntry the root <code>JSONEntry</code>
     */
    public JSON(JSONEntry rootEntry) {
        this.rootEntry = rootEntry;
    }

    /**
     * Exports this JSON as a String. This can be saved to a <code>.json</code> file
     * to be used with other programs and interfaces.
     *
     * @param options formatting options for JSON exporting
     * @return exported JSON
     */
    public String exportJSON(JSONFormatOption... options) {
        JSONFormat format = new JSONFormat(options);
        return rootEntry.getJSONText(0, format);
    }

    /**
     * Returns the root entry of the JSON.
     *
     * @return the root entry as a <code>JSONEntry</code>
     */
    public JSONEntry getRoot() {
        return rootEntry;
    }

    @Override
    public String toString() {
        return exportJSON();
    }
}
