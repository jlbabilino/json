package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class LexicographicalKeys {

    @Test
    void alphabetical() {
        Obj obj = new Obj();
        int jsonFormat = JSONFormat.getFormatCode(0, false, false, false, false, false);
        String desiredJSON = "{\"a\": 1, \"b\": 2, \"c\": 3, \"d\": 4}";
        try {
            String actualJSON = JSONSerializer.serializeJSON(obj).exportJSON(jsonFormat);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @JSONSerializable(JSONType.OBJECT)
    public static class Obj {

        @SerializedJSONObjectValue(key = "b")
        public int b = 2;

        @SerializedJSONObjectValue(key = "a")
        public int a = 1;

        @SerializedJSONObjectValue(key = "d")
        public int d = 4;

        @SerializedJSONObjectValue(key = "c")
        public int c = 3;
    }
}