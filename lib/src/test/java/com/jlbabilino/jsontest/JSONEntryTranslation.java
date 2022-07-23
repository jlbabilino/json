package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class JSONEntryTranslation {

    @Test
    void deserializeJSONEntry() {
        JSONEntry jsonEntry = JSONNumber.of(3);
        try {
            JSONNumber deserialized = JSONDeserializer.deserialize(jsonEntry, JSONNumber.class);
            assertEquals(deserialized.getNumber(), 3);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void serializeJSONEntry() {
        JSONEntry jsonEntry = JSONNumber.of(3);
        try {
            JSONEntry serialized = JSONSerializer.serializeJSONEntry(jsonEntry);
            assertEquals(jsonEntry, serialized);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
}