package com.jlbabilino.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.JSONClassModel;

class InheritanceClassModelTest {

    @Test
    void superInheritance() {
        try {
            JSONClassModel superModel = JSONClassModel.of(Super.class);
            assertEquals(2, superModel.serializedJSONObjectValueFieldsUnmodifiable.size());
            assertEquals(Super.class.getDeclaredField("x"), superModel.serializedJSONObjectValueFieldsUnmodifiable.get(0));
            assertEquals(Super.class.getDeclaredField("y"), superModel.serializedJSONObjectValueFieldsUnmodifiable.get(1));
            assertEquals(2, superModel.serializedJSONObjectValueMethodsUnmodifiable.size());
            assertEquals(Super.class.getDeclaredMethod("method"), superModel.serializedJSONObjectValueMethodsUnmodifiable.get(0).getMethod());
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
    @Test
    void subInheritance() {
        try {
            JSONClassModel subModel = JSONClassModel.of(Sub.class);
            assertEquals(3, subModel.serializedJSONObjectValueFieldsUnmodifiable.size());
            assertEquals(Super.class.getDeclaredField("x"), subModel.serializedJSONObjectValueFieldsUnmodifiable.get(0));
            assertEquals(Super.class.getDeclaredField("y"), subModel.serializedJSONObjectValueFieldsUnmodifiable.get(1));
            assertEquals(Sub.class.getDeclaredField("x"), subModel.serializedJSONObjectValueFieldsUnmodifiable.get(2));
            assertEquals(3, subModel.serializedJSONObjectValueMethodsUnmodifiable.size());
            assertEquals(Sub.class.getDeclaredMethod("method").getAnnotation(SerializedJSONObjectValue.class), subModel.serializedJSONObjectValueMethodsUnmodifiable.get(1).getAnnotation());
            assertEquals(Sub.class.getDeclaredMethod("method"), subModel.serializedJSONObjectValueMethodsUnmodifiable.get(1).getMethod());
            assertEquals(Sub.class.getDeclaredMethod("method", int.class).getAnnotation(SerializedJSONObjectValue.class), subModel.serializedJSONObjectValueMethodsUnmodifiable.get(0).getAnnotation());
            assertEquals(Sub.class.getDeclaredMethod("method", int.class), subModel.serializedJSONObjectValueMethodsUnmodifiable.get(0).getMethod());
            assertEquals(Super.class.getDeclaredMethod("overriden").getAnnotation(SerializedJSONObjectValue.class), subModel.serializedJSONObjectValueMethodsUnmodifiable.get(2).getAnnotation());
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }
}

@JSONSerializable
class Super {

    @SerializedJSONObjectValue(key = "super_x")
    double x;
    @SerializedJSONObjectValue(key = "super_y")
    int y;

    @SerializedJSONObjectValue(key = "super_method")
    int method() {
        return 1;
    }

    @SerializedJSONObjectValue(key = "overriden_annotation")
    void overriden() {
    }
}

class Sub extends Super {

    @SerializedJSONObjectValue(key = "sub_x")
    double x;

    @SerializedJSONObjectValue(key = "sub_method")
    @Override
    int method() {
        return 0;
    }

    @SerializedJSONObjectValue(key = "sub_method_int")
    int method(int x) {
        return x;
    }

    @Override
    void overriden() {
    }

    int dummy;
    Object dummy() {return null;}
}
