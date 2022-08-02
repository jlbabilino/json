package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class MapTranslationTest {

    @Test
    void serializeMap() {
        JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
                JSONString.of("a"), JSONNumber.of(1),
                JSONString.of("b"), JSONNumber.of(2))));
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        try {
            JSON actualJSON = JSONSerializer.serializeJSON(map);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializeMap() {
        Map<String, Integer> desiredMap = new HashMap<>();
        desiredMap.put("a", 1);
        desiredMap.put("b", 2);
        JSON json = JSON.of(JSONObject.of(Map.of(
                JSONString.of("a"), JSONNumber.of(1),
                JSONString.of("b"), JSONNumber.of(2))));
        try {
            Map<String, Integer> actualMap = JSONDeserializer.deserialize(json, new TypeMarker<Map<String, Integer>>() {});
            assertEquals(desiredMap, actualMap);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
}