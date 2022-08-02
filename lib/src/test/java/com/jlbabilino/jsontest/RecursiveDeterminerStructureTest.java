package com.jlbabilino.jsontest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jlbabilino.json.*;
import com.jlbabilino.json.JSONEntry.JSONType;

class RecursiveDeterminerStructureTest {

    @Test
    void recursiveDeterminer1() {
        try {
            JSON desiredRobinJSON = JSON.of(JSONObject.of(Map.of(
                    JSONString.of("kingdom"), JSONString.of("animal"),
                    JSONString.of("species"), JSONString.of("robin"))));
            Lifeform desiredObj = new Lifeform.Animal.Robin();
            JSON serializedRobinJSON = JSONSerializer.serializeJSON(desiredObj);
            assertEquals(desiredRobinJSON, serializedRobinJSON);
            Lifeform deserializedObj = JSONDeserializer.deserialize(desiredRobinJSON, Lifeform.class);
            assertTrue(deserializedObj instanceof Lifeform.Animal.Robin);
        } catch (Exception e) {
            assertTrue(false, e.getMessage());
        }
        
    }

    /**
     * I don't know anything about classification of life, this is a huge simplification.
     */
    @JSONSerializable(JSONType.OBJECT)
    @JSONDeserializable({JSONType.OBJECT})
    public static abstract class Lifeform {

        @SerializedJSONObjectValue(key = "kingdom")
        public abstract String getKingdom();
        @SerializedJSONObjectValue(key = "species")
        public abstract String getSpecies();

        @DeserializedJSONDeterminer
        public static Class<? extends Lifeform> lifeformDeterminer(@DeserializedJSONObjectValue(key = "kingdom") String kingdom)
                throws JSONDeserializerException {
            switch (kingdom) {
                case "plant":
                    return Plant.class;
                case "animal":
                    return Animal.class;
                default:
                    throw new JSONDeserializerException("Unrecognized kingdom");
            }
        }

        public static abstract class Plant extends Lifeform {

            @Override
            public String getKingdom() {
                return "plant";
            }

            @DeserializedJSONDeterminer
            public static Class<? extends Plant> plantDeterminer(@DeserializedJSONObjectValue(key = "species") String species) throws JSONDeserializerException {
                switch (species) {
                    case "pine":
                        return Pine.class;
                    case "sunflower":
                        return Sunflower.class;
                    default:
                        throw new JSONDeserializerException("Unrecognized plant species");
                }
            }

            public static class Pine extends Plant {

                @DeserializedJSONConstructor
                public Pine() {
                }

                @Override
                public String getSpecies() {
                    return "pine";
                }
            }

            public static class Sunflower extends Plant {

                @DeserializedJSONConstructor
                public Sunflower() {
                }

                @Override
                public String getSpecies() {
                    return "sunflower";
                }
            }
        }

        public static abstract class Animal extends Lifeform {

            @DeserializedJSONDeterminer
            public static Class<? extends Animal> plantDeterminer(@DeserializedJSONObjectValue(key = "species") String species) throws JSONDeserializerException {
                switch (species) {
                    case "elephant":
                        return Elephant.class;
                    case "robin":
                        return Robin.class;
                    default:
                        throw new JSONDeserializerException("Unrecognized animal species");
                }
            }

            @Override
            public String getKingdom() {
                return "animal";
            }

            public static class Elephant extends Animal {

                @DeserializedJSONConstructor
                public Elephant() {
                }

                @Override
                public String getSpecies() {
                    return "elephant";
                }
            }
            
            public static class Robin extends Animal {
                
                @DeserializedJSONConstructor
                public Robin() {
                }

                @Override
                public String getSpecies() {
                    return "robin";
                }
            }
        }
    }
}