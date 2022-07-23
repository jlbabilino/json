package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class DeterminerTest {

    @Test
    void deserializeDeterminer() {
        try {
            JSON bJSON = JSON.of(JSONObject.of(Map.of(JSONString.of("type"), JSONString.of("B"))));
            JSON cJSON = JSON.of(JSONObject.of(Map.of(JSONString.of("type"), JSONString.of("C"))));
            A b = JSONDeserializer.deserialize(bJSON, A.class);
            A c = JSONDeserializer.deserialize(cJSON, A.class);
            assertTrue(b instanceof B);
            assertTrue(c instanceof C);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
        
    }

    @JSONDeserializable({JSONType.OBJECT})
    public static interface A {

        @DeserializedJSONDeterminer
        public static Class<? extends A> determiner(@DeserializedJSONObjectValue(key = "type") String typeString) throws JSONDeserializerException {
            switch (typeString) {
                case "B":
                    return B.class;
                case "C":
                    return C.class;
                default:
                    throw new JSONDeserializerException("Unrecognized type string");
            }
        }
    }

    @JSONDeserializable({JSONType.OBJECT})
    public static class B implements A {

        @DeserializedJSONConstructor
        public B() {
        }
    }

    @JSONDeserializable({JSONType.OBJECT})
    public static class C implements A {

        @DeserializedJSONConstructor
        public C() {
        }
    }
}