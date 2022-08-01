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
    JSON(JSONEntry rootEntry) {
        this.rootEntry = rootEntry;
    }

    /**
     * Exports this JSON as a string. This can be saved to a {@code .json} file to
     * be used with other programs and interfaces.
     *
     * @param jsonFormat formatting options for JSON exporting
     * @return the exported JSON
     */
    public String exportJSON(int jsonFormat) {
        return rootEntry.toJSONText(jsonFormat);
    }

    /**
     * Exports this JSON as a string with the default formatting options (see
     * {@link JSONFormat}). This can be saved to a {@code .json} file to be used
     * with other programs and interfaces.
     * 
     * @return the exported JSON
     */
    public String exportJSON() {
        return rootEntry.toJSONText();
    }

    /**
     * Returns the root entry of the JSON.
     *
     * @return the root entry as a <code>JSONEntry</code>
     */
    public JSONEntry getRoot() {
        return rootEntry;
    }

    /**
     * Creates a {@code JSON} given a root entry.
     * 
     * @param rootEntry the root entry
     * @return a {@code JSON} wrapping the root entry
     * @throws NullPointerException if the root entry is {@code null}
     */
    public static JSON of(JSONEntry rootEntry) throws NullPointerException {
        if (rootEntry == null) {
            throw new NullPointerException("Cannot instantiate a JSON with a null root entry.");
        }
        return new JSON(rootEntry);
    }

    @Override
    public int hashCode() {
        return rootEntry.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JSON) {
            return getRoot().equals(((JSON) obj).getRoot());
        } else {
            return false;
        }
    }

    /**
     * Exports this JSON as a {@code String}. Equivalent to
     * 
     * <pre>
     * exportJSON()
     * </pre>
     * 
     * @return the string representation of this JSON
     */
    @Override
    public String toString() {
        return exportJSON();
    }
}
