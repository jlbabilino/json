package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class BigObjectTest {

    @Test
    void serializeBigObject() {
        int sampleCount = 1000; // be careful or you might run out of memory
        SamplePoint[] lotsOfSamples = new SamplePoint[sampleCount];
        for (int i = 0; i < lotsOfSamples.length; i++) {
            lotsOfSamples[i] = new SamplePoint(0, 0, 0, 0, 0, 0, 0);
        }
        try {
            JSONObject sample = JSONObject.of(Map.of(
                    JSONString.of("ts"), JSONNumber.of(0.0),
                    JSONString.of("x"), JSONNumber.of(0.0),
                    JSONString.of("y"), JSONNumber.of(0.0),
                    JSONString.of("theta"), JSONNumber.of(0.0),
                    JSONString.of("vx"), JSONNumber.of(0.0),
                    JSONString.of("vy"), JSONNumber.of(0.0),
                    JSONString.of("omega"), JSONNumber.of(0.0)));
            JSONEntry[] samplesArray = new JSONEntry[sampleCount];
            for (int i = 0; i < samplesArray.length; i++) {
                samplesArray[i] = sample;
            }
            JSON desiredJSON = JSON.of(JSONArray.of(samplesArray));
            JSON actualJSON = JSONSerializer.serializeJSON(lotsOfSamples);
            assertEquals(desiredJSON, actualJSON);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
    }

    @JSONSerializable(JSONType.OBJECT)
    @JSONDeserializable({JSONType.OBJECT, JSONType.ARRAY})
    public static class SamplePoint {

        @SerializedJSONObjectValue(key = "ts")
        public final double ts;
        @SerializedJSONObjectValue(key = "x")
        public final double x;
        @SerializedJSONObjectValue(key = "y")
        public final double y;
        @SerializedJSONObjectValue(key = "theta")
        public final double theta;
        @SerializedJSONObjectValue(key = "vx")
        public final double vx;
        @SerializedJSONObjectValue(key = "vy")
        public final double vy;
        @SerializedJSONObjectValue(key = "omega")
        public final double omega;

        @DeserializedJSONConstructor
        public SamplePoint(
                @DeserializedJSONObjectValue(key = "ts") @DeserializedJSONArrayItem(index = 0) double ts,
                @DeserializedJSONObjectValue(key = "x") @DeserializedJSONArrayItem(index = 1) double x,
                @DeserializedJSONObjectValue(key = "y") @DeserializedJSONArrayItem(index = 2) double y,
                @DeserializedJSONObjectValue(key = "theta") @DeserializedJSONArrayItem(index = 3) double theta,
                @DeserializedJSONObjectValue(key = "vx") @DeserializedJSONArrayItem(index = 4) double vx,
                @DeserializedJSONObjectValue(key = "vy") @DeserializedJSONArrayItem(index = 5) double vy,
                @DeserializedJSONObjectValue(key = "omega") @DeserializedJSONArrayItem(index = 5) double omega) {
            this.ts = ts;
            this.x = x;
            this.y = y;
            this.theta = theta;
            this.vx = vx;
            this.vy = vy;
            this.omega = omega;
        }
    }
}