package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class PoseModelTest {

    @Test
    void serializeTranslation() {
        Translation translation = new Translation();
        translation.x = 0;
        translation.y = 1;
        try {
            JSON actualJSON = JSONSerializer.serializeJSON(translation);
            JSON deseriedJSON = JSON.of(JSONObject.of(Map.of(
                    JSONString.of("x"), JSONNumber.of(0.0),
                    JSONString.of("y"), JSONNumber.of(1.0))));
            assertEquals(deseriedJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void serializePose() {
        Pose pose = new Pose();
        pose.x = 0;
        pose.y = 1;
        pose.heading = 2;
        try {
            JSON actualJSON = JSONSerializer.serializeJSON(pose);
            JSON deseriedJSON = JSON.of(JSONObject.of(Map.of(
            JSONString.of("x"), JSONNumber.of(0.0),
            JSONString.of("y"), JSONNumber.of(1.0),
            JSONString.of("heading"), JSONNumber.of(2.0))));
            assertEquals(deseriedJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializeTranslation() {
        JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
                JSONString.of("x"), JSONNumber.of(0.0),
                JSONString.of("y"), JSONNumber.of(1.0))));
        try {
            Translation translation = JSONDeserializer.deserialize(desiredJSON, Translation.class);
            JSON actualJSON = JSONSerializer.serializeJSON(translation);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializeTranslationPose() {
        JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
                JSONString.of("x"), JSONNumber.of(0.0),
                JSONString.of("y"), JSONNumber.of(1.0),
                JSONString.of("heading"), JSONNumber.of(2.0))));
        try {
            // shoudl figure out that it's actually a Pose:
            Translation pose = JSONDeserializer.deserialize(desiredJSON, Translation.class);
            JSON actualJSON = JSONSerializer.serializeJSON(pose);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @Test
    void deserializePose() {
        JSON desiredJSON = JSON.of(JSONObject.of(Map.of(
            JSONString.of("x"), JSONNumber.of(0.0),
            JSONString.of("y"), JSONNumber.of(1.0),
            JSONString.of("heading"), JSONNumber.of(2.0))));
        try {
            Pose pose = JSONDeserializer.deserialize(desiredJSON, Pose.class);
            JSON actualJSON = JSONSerializer.serializeJSON(pose);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        } 
    }

    @JSONSerializable(JSONType.OBJECT)
    @JSONDeserializable({JSONType.OBJECT})
    public static class Translation {

        @SerializedJSONObjectValue(key = "x")
        @DeserializedJSONObjectValue(key = "x")
        public double x;

        public double y;

        @DeserializedJSONConstructor
        public Translation() {
        }

        @DeserializedJSONTarget
        public void setY(@DeserializedJSONObjectValue(key = "y") double y) {
            this.y = y;
        }

        @SerializedJSONObjectValue(key = "y")
        public double getY() {
            return y;
        }

        @DeserializedJSONDeterminer
        public static Class<? extends Translation> determiner(@DeserializedJSONEntry JSONObject jsonObject) {
            if (jsonObject.containsKey("heading")) {
                return Pose.class;
            } else {
                return Translation.class;
            }
        }
    }

    public static class Pose extends Translation {

        @SerializedJSONObjectValue(key = "heading")
        @DeserializedJSONObjectValue(key = "heading")
        public double heading;

        @DeserializedJSONConstructor
        public Pose() {
        }
    }

}