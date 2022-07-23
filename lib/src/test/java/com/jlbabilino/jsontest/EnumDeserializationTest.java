package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class EnumDeserializationTest {

    @Test
    void deserializeEnum() {
        JSONString toStringMethod = JSONString.of("sticky_myEnum"); // must be exact with toString
        JSONString nameMethod = JSONString.of("sticky");
        JSONString nameAlternateCaseMethod = JSONString.of("STICKY");
        JSONString nameTrimMethod = JSONString.of(" stI_cky "); // pretty relaxed with this one
        try {
            MyEnum desiredEnum = MyEnum.STICKY;
            assertEquals(desiredEnum, JSONDeserializer.deserialize(toStringMethod, MyEnum.class));
            assertEquals(desiredEnum, JSONDeserializer.deserialize(nameMethod, MyEnum.class));
            assertEquals(desiredEnum, JSONDeserializer.deserialize(nameAlternateCaseMethod, MyEnum.class));
            assertEquals(desiredEnum, JSONDeserializer.deserialize(nameTrimMethod, MyEnum.class));
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    public static enum MyEnum {
        HARD, SOFT, LONG, SHORT, STICKY, BIG;

        public String toString() {
            return name().toLowerCase() + "_myEnum";
        }
    }
}