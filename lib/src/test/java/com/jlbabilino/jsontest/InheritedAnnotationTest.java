package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class InheritedAnnotationTest {

    @Test
    void subSerialize() {
        Sub subObj = new Sub();
        try {
            JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
                    JSONString.of("super"), JSONNumber.of(1))));
            JSON actualJSON = JSONSerializer.serializeJSON(subObj);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @JSONSerializable(JSONType.OBJECT)
    public static class Super {
        @SerializedJSONObjectValue(key = "super")
        public int method() {
            return 0;
        }
    }
    public static class Sub extends Super {
        @Override
        public int method() {
            return 1;
        }
    }
}