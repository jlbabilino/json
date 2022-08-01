package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class EnumDeserializationTest {

    @Test
    void serializeEnum() {
        try {
            JSONString hardString = JSONSerializer.serializeJSONEntry(MyEnum.HARD).asString();
            assertEquals(hardString, JSONString.of("hard_myEnum"));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializeEnum() {
        JSONString toStringMethod = JSONString.of("sticky_myEnum");
        JSONString nameMethod = JSONString.of("STICKY");
        try {
            assertEquals(MyEnum.STICKY, JSONDeserializer.deserialize(toStringMethod, MyEnum.class));
            assertEquals(MyEnum.STICKY, JSONDeserializer.deserialize(nameMethod, MyEnum.class));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializeSensitiveCasesEnum() {
        JSONString underScore = JSONString.of("UNDERSCORE");
        JSONString under_Score = JSONString.of("UNDER_SCORE");
        JSONString lowerCase = JSONString.of("lowerCase");

        try {
            MyEnum desiredEnum = MyEnum.STICKY;
            assertEquals(MyEnum.UNDERSCORE, JSONDeserializer.deserialize(underScore, MyEnum.class));
            assertEquals(MyEnum.UNDER_SCORE, JSONDeserializer.deserialize(under_Score, MyEnum.class));
            assertEquals(MyEnum.lowerCase, JSONDeserializer.deserialize(lowerCase, MyEnum.class));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    public static enum MyEnum {
        HARD, SOFT, LONG, SHORT, STICKY, BIG, UNDERSCORE, UNDER_SCORE, lowerCase;

        public String toString() {
            return name().toLowerCase() + "_myEnum";
        }
    }
}