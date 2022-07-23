package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class StringEscapeTest {

    @Test
    void escapeString() {
        String unescapedString = "\"hello\"\\\n world\t";
        String escapedString = "\\\"hello\\\"\\\\\\n world\\t";
        assertEquals(escapedString, JSONSerializer.escapeString(unescapedString));
    }

    @Test
    void unescapeString() {
        String escapedString = "\\\"hello\\\"\\\\\\n world\\t";
        String unescapedString = "\"hello\"\\\n world\t";
        try {
            assertEquals(unescapedString, JSONDeserializer.unescapeString(escapedString));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
}