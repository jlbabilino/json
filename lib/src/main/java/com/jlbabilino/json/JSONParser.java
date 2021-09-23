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

import static com.jlbabilino.json.JSONNull.NULL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class takes a <code>String</code> input, and outputs a
 * <code>JSON</code>. This is only possible if the <code>String</code> is valid
 * JSON data. If it is not, exceptions will be thrown to indicate the issue to
 * the user.
 *
 * @author Justin Babilino
 */
public class JSONParser {

    /**
     * A string matches this regex if and only if it complies with the JSON standard
     * for numbers
     */
    // public static Pattern jsonNumberRegex =
    // Pattern.compile("(0|[1-9])\\d*(\\.\\d*)?([eE]\\d+)?"); // old
    // "(0|[1-9]*)(\\.\\d*)?([eE]\\d+)?"
    /**
     * The <code>JSON</code> where all parsed data is dumped to
     */
    private JSON json;
    /**
     * The string where JSON data is gathered from.
     */
    private final String jsonString;

    /**
     * The current index in <code>jsonString</code>.
     */
    private int index;
    /**
     * The current line number in the file, used to communicate error information to
     * the user.
     */
    private int lineNumber;

    /**
     * Constructs a <code>JSONParser</code> with a <code>String</code> representing
     * the text in the JSON file.
     *
     * @throws JSONParserException if the JSON was not able to be parsed
     * @param jsonString the <code>String</code> data from the JSON file
     */
    private JSONParser(String jsonString) throws JSONParserException {
        this.jsonString = jsonString;
        index = 0;
        lineNumber = 1; // line num starts at 1 is typically the convention
        parse(); // Automatically parse at construction.
    }

    public static JSON parseString(String jsonString) throws JSONParserException {
        JSONParser parser = new JSONParser(jsonString);
        return parser.getJSON();
    }

    /**
     * This method parses <code>jsonString</code> into a JSON.
     *
     * @throws JSONParserException if there is an error during parsing
     */
    private void parse() throws JSONParserException {
        skipWhitespace();
        JSONEntry rootEntry = entry();
        json = new JSON(rootEntry);
    }

    /**
     * This method increments the index enough to go past line breaks and spaces. It
     * works with all types of newline sequences on all systems.
     */
    private void skipWhitespace() {
        while (isWhitespace()) {
            if (isNewline()) { // this is only triggered if the index is at beginning of newline sequence
                lineNumber++;
            }
            index++;
        }
    }

    /**
     * <p>
     * <b>Precondition: </b> the index must be at the beginning of a new JSON entry.
     * </p>
     * <p>
     * This method determines which type of JSON entry is in the text block that it
     * starts on, returning a new <code>JSONEntry</code> with the appropriate data.
     * </p>
     *
     * @return the <code>JSONEntry</code> for the text block beginning on
     * @throws JSONParserException if there is no entry found in the text block
     */
    private JSONEntry entry() throws JSONParserException {
        JSONEntry entry;
        char initialChar = charAtIndex();
        switch (initialChar) {
            case '{':
                entry = objectEntry();
                break;
            case '[':
                entry = arrayEntry();
                break;
            case '"':
                entry = stringEntry();
                break;
            case 't':
                entry = booleanEntry(true);
                break;
            case 'f':
                entry = booleanEntry(false);
                break;
            case 'n':
                entry = nullEntry();
                break;
            default:
                if (isCharNumber(initialChar)) {
                    entry = numberEntry();
                } else {
                    throw new JSONParserException(index, lineNumber,
                            "Expecting '{', '[', Boolean, Number, String, Null, got: '" + initialChar + "'");
                }
                break;
        }
        return entry;
    }

    /**
     * This method parses block of JSON text of object type and generates a
     * <code>JSONObjectEntry</code> with the appropriate data.
     *
     * @return a <code>JSONObjectEntry</code> with the data associated with the
     *         block of text
     * @throws JSONParserException if there is improper syntax in the object
     */
    private JSONObject objectEntry() throws JSONParserException {
        int startIndex = index; // save index location while testing for blank object
        int startLineNum = lineNumber; // save the line number while testing
        index++; // skip past initial curly brace {
        skipWhitespace(); // possibly skip to close brace
        if (charAtIndex() == '}') {
            index++; // skip past final close brace }
            return new JSONObject(new HashMap<>()); // if it is blank, return blank object
        }
        index = startIndex; // return back to original start index if not blank
        lineNumber = startLineNum;

        Map<String, JSONEntry> entries = new HashMap<>();
        do {
            index++; // skip past either beginning of object { or previous comma ,
            skipWhitespace(); // skip to key
            String key = readString();
            skipWhitespace(); // skip to colon :
            if (charAtIndex() != ':') {
                throw new JSONParserException(index, lineNumber, "Expecting ':', got '" + charAtIndex() + "'");
            }
            index++; // skip past colon :
            skipWhitespace(); // skip whitespace between colon : and entry
            JSONEntry entry = entry();
            entries.put(key, entry); // put keyed entry in map
            skipWhitespace(); // skip to comma , or end of object }
        } while (charAtIndex() == ','); // keep checking if there is another entry
        if (charAtIndex() != '}') {
            throw new JSONParserException(index, lineNumber, "Expecting '}', got '" + charAtIndex() + "'");
        }
        index++; // skip closing curly brace }
        return new JSONObject(entries);
    }

    /**
     * This method parses an array block of JSON text and generates the appropriate
     * <code>ArrayJSONEntry</code>.
     *
     * @return an <code>ArrayJSONEntry</code> with the appropriate data
     * @throws JSONParserException if there is a syntax error in the text block
     */
    private JSONArray arrayEntry() throws JSONParserException {
        int startIndex = index; // save index location while testing for blank array
        int startLineNum = lineNumber; // save the line number while testing
        index++; // skip past initial bracket [
        skipWhitespace(); // possibly skip to close bracket ]
        if (charAtIndex() == ']') {
            index++; // skip past final close bracket ]
            return new JSONArray(new JSONEntry[0]);
        }
        index = startIndex; // return back to original start index if not blank
        lineNumber = startLineNum;

        List<JSONEntry> entries = new ArrayList<>();
        do {
            index++; // either skip initial opening square bracket [ or skip previous comma ,
            skipWhitespace(); // skip space between previous comma , or opening square bracket [ and next
                              // entry
            entries.add(entry());
            skipWhitespace(); // skip to comma or end of array ]
        } while (charAtIndex() == ','); // keep looping if there is another entry
        if (charAtIndex() != ']') { // this needs to be closing or syntax is bad
            throw new JSONParserException(index, lineNumber, "Expecting ']', got '" + charAtIndex() + "'");
        }
        index++; // skip closing bracket ]
        return new JSONArray(entries.toArray(new JSONEntry[0])); // Convert list to array and build new Array entry
    }

    /**
     * <b>Precondition: </b> the index must be placed at the beginning of a String
     * JSON entry.
     * <p>
     * This method parses a String entry from a block of string JSON text. It then
     * generates and returns a <code>StringJSONEntry</code> with the appropriate
     * data.
     * </p>
     *
     * @return the string entry associated with the block of text
     * @throws JSONParserException if the string syntax is invalid
     */
    private JSONString stringEntry() throws JSONParserException {
        return new JSONString(readString());
    }

    /**
     * <b>Precondition: </b> the index must be placed at the beginning of a Number
     * JSON entry.
     * <p>
     * This method parses a Number entry from block of number JSON text.
     * </p>
     *
     * @return a <code>NumberJSONEntry</code> with the appropriate number value
     * @throws JSONParserException if there is a syntax error in the number
     */
    private JSONNumber numberEntry() throws JSONParserException {
        int beginIndex = index;
        do {
            index++;
        } while (isCharNumber(charAtIndex()));
        String numberString = jsonString.substring(beginIndex, index);
        Number number;
        if (numberString.contains(".")) {
            try {
                number = Double.parseDouble(numberString); // first try double then go to big decimal if doesn't work
            } catch (NumberFormatException doubleE) {
                try {
                    number = new BigDecimal(numberString);
                } catch (NumberFormatException bigDecimalE) {
                    throw new JSONParserException(index, lineNumber, "Expecting Number, got \"" + numberString + "\"");
                }
            }
        } else {
            try {
                number = Integer.parseInt(numberString); // first try integer then go to big integer if doesn't work
            } catch (NumberFormatException integerE) {
                try {
                    number = new BigInteger(numberString);
                } catch (NumberFormatException bigIntegerE) {
                    throw new JSONParserException(index, lineNumber, "Expecting Number, got \"" + numberString + "\"");
                }
            }
        }
        return new JSONNumber(number);
    }

    /**
     * <b>Precondition: </b> the index must be placed at the beginning of a boolean
     * JSON entry.
     * <p>
     * Parses a boolean string in a JSON and returns the appropriate
     * <code>BooleanJSONEntry</code>.
     * 
     * @param value predicted value of JSON boolean
     * @return a <code>BooleanJSONEntry</code> with the appropriate boolean value
     * @throws JSONParserException if there is a syntax error in the boolean
     */
    private JSONBoolean booleanEntry(boolean value) throws JSONParserException {
        if (value) { // if true
            String boolString = jsonString.substring(index, index + 4);
            if (boolString.equals("true")) {
                index += 4; // if "true" add four to index to skip past
            } else {
                throw new JSONParserException(index, lineNumber, "Expecting \"true\", got \"" + boolString + "\"");
            }
        } else { // if false
            String boolString = jsonString.substring(index, index + 5);
            if (boolString.equals("false")) {
                index += 5; // if "false" add five to index to skip past
            } else {
                throw new JSONParserException(index, lineNumber, "Expecting \"false\", got \"" + boolString + "\"");
            }
        }
        return new JSONBoolean(value);
    }

    /**
     * <p>
     * <b>Precondition: </b> <code>charAt(index)</code> must be 'n'
     * </p>
     * <p>
     * This method verifies that the string at the index equals "null", skips past
     * the null and returns a <code>NullJSONEntry</code>. If the string does not
     * equal "null", throws a <code>JSONParserException</code>
     * </p>
     *
     * @return a new <code>NullJSONEntry</code> to represent the null value
     * @throws JSONParserException if the string does not contain "null"
     */
    protected JSONEntry nullEntry() throws JSONParserException {
        String nullString = jsonString.substring(index, index + 4);
        if (nullString.equals("null")) {
            index += 4;
            return NULL;
        } else {
            throw new JSONParserException(index, lineNumber, "Expecting \"null\", got \"" + nullString + "\"");
        }
    }

    /**
     * Returns the character at the index provided.
     *
     * @param index the index
     * @return the character
     */
    private char charAt(int index) {
        if (index >= jsonString.length()) {
            return ' ';
        }
        return jsonString.charAt(index);
    }

    /**
     * Returns the character at the current index.
     *
     * @return the character
     */
    private char charAtIndex() {
        return charAt(index);
    }

    /**
     * Checks if the character at the index is any character that is valid in a JSON
     * number. This includes digits (0, 1, 2, ... , 8, 9), minus '-', the exponent
     * characters 'e' and 'E', and the decimal point '.'.
     *
     * @param ch the character to check
     * @return true if the character is a valid JSON number character, false
     *         otherwise
     */
    private boolean isCharNumber(char ch) {
        return Character.isDigit(ch) || ch == '-' || ch == 'e' || ch == 'E' || ch == '.'; // technically this allows
                                                                                          // other languages
                                                                                          // but doesn't matter because
                                                                                          // it will be
                                                                                          // caught by numberEntry()
                                                                                          // anyways
    }

    /**
     * Checks if the character at the index is a whitespace character.
     *
     * @return true if a whitespace character, false otherwise
     */
    private boolean isWhitespace() {
        return Character.isWhitespace(charAtIndex());
    }

    /**
     * Checks if the characters beginning at the index form a newline sequence. This
     * is system independent and checks for the newline that is used by your system.
     *
     * @return true if newline sequence, false otherwise
     */
    private boolean isNewline() {
        return jsonString.substring(index, index + System.lineSeparator().length()).equals(System.lineSeparator());
    }

    /**
     * <p>
     * Takes a string at the index like this:
     * </p>
     * <p>
     * <code>"test"</code>
     * </p>
     * <p>
     * And returns a string like this:
     * </p>
     * <p>
     * <code>test</code>
     * </p>
     * <p>
     * Essentially, this method removes the quotes from both sides and returns it as
     * a String. It also increments the index past the last quote.
     * </p>
     *
     * @return the string found at the current index with double quotes removed
     * @throws JSONParserException if the string at the index has a syntax error
     */
    protected String readString() throws JSONParserException {
        if (charAtIndex() != '"') {
            throw new JSONParserException(index, lineNumber, "Expecting String, got '" + charAtIndex() + "'");
        }
        index++; // skip past initial double quote "
        int stringLength = 0;
        while (charAtIndex() != '"' || charAt(index - 1) == '\\') {
            if (charAtIndex() == '\\') {

            }
            if (isNewline()) {
                throw new JSONParserException(index, lineNumber, "Missing end \" to close String");
            }
            index++;
            stringLength++;
        } // count length of name and move index to final double quote "
        String string = jsonString.substring(index - stringLength, index);
        index++; // skip past final double quote
        return string;
    }

    /**
     * Returns the parsed <code>JSON</code>.
     *
     * @return the <code>JSON</code>
     */
    public JSON getJSON() {
        return json;
    }
}
