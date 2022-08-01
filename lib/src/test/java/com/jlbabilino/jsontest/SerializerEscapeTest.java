package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class SerializerEscapeTest {

    @Test
    void escapeCaseTest() {
        try {
            Obj obj = new Obj();
            JSONObject desiredJSON = JSONObject.of(Map.of(JSONString.of("\""), JSONString.of("\""))); // JSONString.of escapes the string
            JSONObject actualJSON = JSONSerializer.serializeJSONEntry(obj).asObject();
            assertEquals(desiredJSON, actualJSON);
            assertEquals(desiredJSON.get("\\\""), JSONString.of("\""));
            assertEquals(JSONString.of("\"").getString(), "\\\"");
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @JSONSerializable(JSONType.OBJECT)
    public static class Obj {

        @SerializedJSONObjectValue(key = "\"")
        public String getStr() {
            return "\"";
        }
    }
}