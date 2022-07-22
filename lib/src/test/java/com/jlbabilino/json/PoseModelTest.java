package com.jlbabilino.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.JSON;
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
                    JSONString.of("x"), JSONNumber.of(0),
                    JSONString.of("y"), JSONNumber.of(1))));
            assertEquals(deseriedJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void serializePose() {
        Pose pose = new Pose();
        pose.x = 2;
        pose.y = 3;
        pose.heading = 4;
        try {
            JSON actualJSON = JSONSerializer.serializeJSON(pose);
            JSON deseriedJSON = new JSON(JSONObject.of(Map.of(
            JSONString.of("x"), JSONNumber.of(2),
            JSONString.of("y"), JSONNumber.of(3),
            JSONString.of("heading"), JSONNumber.of(4))));
            assertEquals(deseriedJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    void deserializeTranslation() {

    }

    @Test
    void deserializeTranslationPose() {

    }

    @Test
    void deserializePose() {

    }
}

@JSONSerializable(JSONType.OBJECT)
@JSONDeserializable({JSONType.OBJECT})
class Translation {

    @SerializedJSONObjectValue(key = "x")
    @DeserializedJSONObjectValue(key = "y")
    double x;

    double y;

    @DeserializedJSONConstructor
    Translation() {
    }

    @DeserializedJSONTarget
    void setY(@DeserializedJSONObjectValue(key = "y") double y) {
        this.y = y;
    }

    @SerializedJSONObjectValue(key = "y")
    double getY() {
        return y;
    }

    @DeserializedJSONDeterminer
    private Class<? extends Translation> determiner(@DeserializedJSONEntry JSONObject jsonObject) {
        if (jsonObject.containsKey("heading")) {
            return Pose.class;
        } else {
            return Translation.class;
        }
    }
}

class Pose extends Translation {

    @SerializedJSONObjectValue(key = "heading")
    @DeserializedJSONObjectValue(key = "heading")
    double heading;

    @DeserializedJSONConstructor
    Pose() {
    }
}
