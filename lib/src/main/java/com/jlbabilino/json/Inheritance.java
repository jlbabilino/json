package com.jlbabilino.json;

public class Inheritance {
    @JSONSerializable
    static class Super {
        private int x;
        @DeserializedJSONConstructor
        public Super() {
            x = 0;
        }
        @SerializedJSONObjectValue(key = "x")
        public int getX() {
            return x;
        }
        @DeserializedJSONTarget
        public void setX(@DeserializedJSONObjectValue(key = "x") int x) {
            this.x = x;
        }
    }
    static class Sub extends Super {
        @Override
        public int getX() {
            return 1;
        }
    }

    public static void main(String[] args) {
        Sub sub = new Sub();
        JSONEntry jsonEntry = JSONSerializer.serializeJSONEntry(sub);
        assert jsonEntry.asObject().get("x").asNumber().getNumber().equals(1);
    }
}