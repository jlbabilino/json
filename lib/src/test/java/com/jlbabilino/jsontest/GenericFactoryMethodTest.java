package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class GenericFactoryMethodTest {

    @Test
    void genericFactoryMethod() {
        try {
            JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
                    JSONString.of("a"), JSONString.of("15"),
                    JSONString.of("b"), JSONNumber.of(16))));
            Obj<String, Integer> actualObj = JSONDeserializer.deserialize(desiredJSON, new TypeMarker<Obj<String, Integer>>() {});
            JSON actualJSON = JSONSerializer.serializeJSON(actualObj);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @JSONSerializable(JSONType.OBJECT)
    @JSONDeserializable({JSONType.OBJECT})
    public static class Obj<A, B extends Number> {

        @SerializedJSONObjectValue(key = "a")
        public final A a;
        @SerializedJSONObjectValue(key = "b")
        public final B b;

        private Obj(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @DeserializedJSONConstructor
        public static <C, D extends Number> Obj<C, D> factory(@DeserializedJSONObjectValue(key = "a") C c, @DeserializedJSONObjectValue(key = "b") D d) {
            return new Obj<>(c, d);
        }
    }
}