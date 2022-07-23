package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class JSONParserTest {

    @Test
    void parseObject() {
        String object0Str = "{}";
        String object1Str = "{\"item\": 65e3, \"subObj\": {\"sub item\": \"str\"}}";
        try {
            JSONEntry object0 = JSONObject.of(Map.of());
            JSONEntry object1 = JSONObject.of(Map.of(
                    JSONString.of("item"), JSONNumber.of(65e3),
                    JSONString.of("subObj"), JSONObject.of(Map.of(
                            JSONString.of("sub item"), JSONString.of("str")))));
            assertEquals(object0, JSONParser.parseJSONEntry(object0Str));
            assertEquals(object1, JSONParser.parseJSONEntry(object1Str));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
        
    }

    @Test
    void parseNumber() {
        try {
            JSONNumber desiredInteger = JSONNumber.of(-32);
            assertEquals(desiredInteger, JSONParser.parseJSONEntry("-32"));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
        
    }
}