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
 * This class models all formatting options for JSON exporting. It allows for
 * the adjustment of indent spaces and array and object newline settings.
 * </p>
 * <p>
 * Here are some things that I would like to add later:
 * </p>
 * <ol>
 * <li>Number formatting</li>
 * </ol>
 *
 * @author Justin Babilino
 */
public class JSONFormat {

    /**
     * Stores a cache of all possible indent strings that use spaces
     */
    private static final String[] indentSpacesStrings = new String[16];
    /**
     * Stores a cache of all possible indent strings that use tabs
     */
    private static final String[] indentTabsStrings = new String[16];

    static {
        indentSpacesStrings[0] = "";
        for (int i = 1; i < 16; i++) {
            indentSpacesStrings[i] = indentSpacesStrings[i - 1] + " ";
        }
        indentTabsStrings[0] = "";
        for (int i = 1; i < 16; i++) {
            indentTabsStrings[i] = indentTabsStrings[i - 1] + "\t";
        }
    }

    /**
     * The bits in a JSON format code that indicate the number of spaces
     */
    public static final int INDENT_SPACES_BITS = 0b000001111;
    /**
     * The bit in a JSON format code that if equal to {@code 0} indicates that
     * <i>spaces</i> should be used for indents and if equal to {@code 1} indicates
     * that <i>tabs</i> shold be used.
     */
    public static final int USE_TABS_BIT = 0b000010000;
    /**
     * The bit in a JSON format code that if equal to {@code 0} indicates that
     * newlines <i>should not</i> be placed between each item in a JSON array and if
     * equal to {@code 1} indicates that newlines <i>should</i> be placed between
     * each item.
     */
    public static final int ARRAY_NEWLINE_PER_ITEM_BIT = 0b000100000;
    /**
     * The bit in a JSON format code that if equal to {@code 0} indicates that the
     * opening square bracket {@code [} in a JSON array <i>should not</i> be placed
     * on a newline and if equal to {@code 1} indicates that the opening square
     * bracket {@code [} <i>should</i> be placed on a newline.
     */
    public static final int ARRAY_BEGIN_ON_NEWLINE_BIT = 0b001000000;
    /**
     * The bit in a JSON format code that if equal to {@code 0} indicates that
     * newlines <i>should not</i> be placed between each item in a JSON object and
     * if equal to {@code 1} indicates that newlines <i>should</i> be placed between
     * each item.
     */
    public static final int OBJECT_NEWLINE_PER_ITEM_BIT = 0b010000000;
    /**
     * The bit in a JSON format code that if equal to {@code 0} indicates that the
     * opening curly brace <code>{</code> in a JSON object <i>should not</i> be
     * placed on a newline and if equal to {@code 1} indicates that the opening
     * curly brace <code>{</code> <i>should</i> be placed on a newline.
     */
    public static final int OBJECT_BEGIN_ON_NEWLINE_BIT = 0b100000000;

    /**
     * The default format code used when no format code is provided
     */
    public static final int DEFAULT_FORMAT_CODE = 0b010100100;

    /**
     * Prevent instantiation
     */
    private JSONFormat() {
    }

    /**
     * Generates a JSON format code with specified options.
     * 
     * @param indentSpacesCount    the number of spaces or tabs in indents, from
     *                             {@code 0} to {@code 15}
     * @param useTabs              if {@code true}, use tabs instead of spaces in
     *                             indents
     * @param arrayNewlinePerItem  if {@code true}, include line breaks between
     *                             array items
     * @param arrayBeginOnNewline  if {@code true}, begin arrays on newlines
     * @param objectNewlinePerItem if {@code true}, include line breaks between
     *                             object items
     * @param objectBeginOnNewline if {@code true}, begin objects on newlines
     * @return the generated JSON format code, as an {@code int}
     */
    public static int getFormatCode(int indentSpacesCount, boolean useTabs, boolean arrayNewlinePerItem,
            boolean arrayBeginOnNewline, boolean objectNewlinePerItem, boolean objectBeginOnNewline) {
        if (indentSpacesCount < 0) {
            indentSpacesCount = 0;
        } else if (indentSpacesCount > INDENT_SPACES_BITS) {
            indentSpacesCount = INDENT_SPACES_BITS;
        }
        int formatCode = indentSpacesCount;
        formatCode += useTabs ? USE_TABS_BIT : 0;
        formatCode += arrayNewlinePerItem ? ARRAY_NEWLINE_PER_ITEM_BIT : 0;
        formatCode += arrayBeginOnNewline ? ARRAY_BEGIN_ON_NEWLINE_BIT : 0;
        formatCode += objectNewlinePerItem ? OBJECT_NEWLINE_PER_ITEM_BIT : 0;
        formatCode += objectBeginOnNewline ? OBJECT_BEGIN_ON_NEWLINE_BIT : 0;
        return formatCode;
    }

    /**
     * Gets the number of indent spaces from a JSON format code.
     * 
     * @param jsonFormat the JSON format code
     * @return the {@code int} value of spaces
     */
    public static int indentSpacesCount(int jsonFormat) {
        return jsonFormat & INDENT_SPACES_BITS;
    }

    /**
     * Returns {@code true} if the JSON format code uses tabs and {@code false}
     * otherwise.
     * 
     * @param jsonFormat the JSON format code
     * @return {@code true} if the JSON format code uses tabs, {@code false}
     *         otherwise
     */
    public static boolean useTabs(int jsonFormat) {
        return (jsonFormat & USE_TABS_BIT) == USE_TABS_BIT;
    }

    /**
     * Returns {@code true} if the JSON format code uses newlines between array
     * items and {@code false} otherwise.
     * 
     * @param jsonFormat the JSON format code
     * @return {@code true} if the JSON format code uses newlines between array
     *         items, {@code false} otherwise
     */
    public static boolean arrayNewlinePerItem(int jsonFormat) {
        return (jsonFormat & ARRAY_NEWLINE_PER_ITEM_BIT) == ARRAY_NEWLINE_PER_ITEM_BIT;
    }

    /**
     * Returns {@code true} if the JSON format code begins arrays on newlines and
     * {@code false} otherwise.
     * 
     * @param jsonFormat the JSON format code
     * @return {@code true} if the JSON format code begins arrays on newlines,
     *         {@code false} otherwise
     */
    public static boolean arrayBeginOnNewline(int jsonFormat) {
        return (jsonFormat & ARRAY_BEGIN_ON_NEWLINE_BIT) == ARRAY_BEGIN_ON_NEWLINE_BIT;
    }

    /**
     * Returns {@code true} if the JSON format code uses newlines between object
     * items and {@code false} otherwise.
     * 
     * @param jsonFormat the JSON format code
     * @return {@code true} if the JSON format code uses newlines between object
     *         items, {@code false} otherwise
     */
    public static boolean objectNewlinePerItem(int jsonFormat) {
        return (jsonFormat & OBJECT_NEWLINE_PER_ITEM_BIT) == OBJECT_NEWLINE_PER_ITEM_BIT;
    }

    /**
     * Returns {@code true} if the JSON format code begins objects on newlines and
     * {@code false} otherwise.
     * 
     * @param jsonFormat the JSON format code
     * @return {@code true} if the JSON format code begins objects on newlines,
     *         {@code false} otherwise
     */
    public static boolean objectBeginOnNewline(int jsonFormat) {
        return (jsonFormat & OBJECT_BEGIN_ON_NEWLINE_BIT) == OBJECT_BEGIN_ON_NEWLINE_BIT;
    }

    /**
     * Gets the indent string from a JSON format code. For example, if you put in a
     * JSON format code with an indent spaces count of 6 and useTabs set to false,
     * then this method would return a {@code String} with six spaces. These strings
     * are all cached in the system for memory efficiency.
     * 
     * @param jsonFormat the JSON format code
     * @return the indent string
     */
    public static String getIndentString(int jsonFormat) {
        int spacesCount = indentSpacesCount(jsonFormat);
        boolean useTabs = useTabs(jsonFormat);
        if (useTabs) {
            return indentTabsStrings[spacesCount];
        } else {
            return indentSpacesStrings[spacesCount];
        }
    }
}
