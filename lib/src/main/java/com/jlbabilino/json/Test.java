package com.jlbabilino.json;

import java.lang.reflect.Field;

import com.jlbabilino.json.JSONEntry.JSONType;

public class Test {
    public static void main(String[] args) throws JSONDeserializerException {
        // TypeMarker<TwoTuple<String, Integer>> TYPE = new TypeMarker<>() {};
        // JSONEntry jsonData = JSONSerializer.serializeJSONEntry(new Object[]{"hi", 20});
        // TwoTuple<String, Integer> deserializedData = JSONDeserializer.deserialize(jsonData, TYPE);
        // System.out.println(JSONSerializer.serializeString(deserializedData));
        // Waypoint waypoint = new Waypoint(1, 2);
        // HeadWaypoint headWaypoint = new HeadWaypoint(3, -1, -4);
        // JSON waypointJSON = JSONSerializer.serializeJSON(waypoint);
        // JSON headWaypointJSON = JSONSerializer.serializeJSON(headWaypoint);
        Waypoint waypoint = JSONDeserializer.deserialize("{\"x\": 1, \"y\": 2}", Waypoint.class);
        Waypoint headWaypoint = JSONDeserializer.deserialize("{\"x\": 3, \"y\": -1, \"head\": -4}", Waypoint.class);
        System.out.println(JSONSerializer.serializeString(waypoint));
        System.out.println(JSONSerializer.serializeString(headWaypoint));

    }
}

@JSONSerializable(rootType = JSONType.ARRAY)
class TwoTuple<A, B> {
    @SerializedJSONArrayItem(index = 0)
    public final A a;
    @SerializedJSONArrayItem(index = 1)
    public final B b;

    private TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @DeserializedJSONConstructor
    public static <C, D> TwoTuple<C, D> of(@DeserializedJSONArrayItem(index = 0) C a, @DeserializedJSONArrayItem(index = 1) D b) {
        return new TwoTuple<>(a, b);
    }
}

@JSONSerializable
class Waypoint {

    @SerializedJSONObjectValue(key = "x")
    public final double x;
    @SerializedJSONObjectValue(key = "y")
    public final double y;

    @DeserializedJSONConstructor
    public Waypoint(@DeserializedJSONObjectValue(key = "x") double x, @DeserializedJSONObjectValue(key = "y") double y) {
        this.x = x;
        this.y = y;
    }

    @DeserializedJSONDeterminer
    public static Class<? extends Waypoint> determiner(@DeserializedJSONEntry JSONObject obj) throws JSONDeserializerException {
        if (obj.containsKey("head")) {
            return HeadWaypoint.class;
        } else {
            return Waypoint.class;
        }
    }
}

@JSONSerializable
class HeadWaypoint extends Waypoint {

    @SerializedJSONObjectValue(key = "head")
    public final double head;

    @DeserializedJSONConstructor
    public HeadWaypoint(@DeserializedJSONObjectValue(key = "x") double x, @DeserializedJSONObjectValue(key = "y") double y, @DeserializedJSONObjectValue(key = "head") double head) {
        super(x, y);
        this.head = head;
    }
}

class Super {
    public static void main(String[] args) throws Exception {
        Sub sub = new Sub();
        Field superField = Super.class.getDeclaredField("x");
        Field subField = Sub.class.getDeclaredField("x");
        superField.set(sub, 5);
        subField.set(sub, 3);
        System.out.println(superField.get(sub));
        System.out.println(subField.get(sub));
    }
    public int x;
}

class Sub extends Super {
    public int x;
}

