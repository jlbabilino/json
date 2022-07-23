package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;

class NumberTest {

    @Test
    void serializeInteger() {
        int x = 3;
        try {   
            assertEquals(JSONNumber.of(x).toString(), "3");
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
}