package com.jlbabilino.json.examples.maptests;

import com.jlbabilino.json.DeserializedJSONConstructor;
import com.jlbabilino.json.DeserializedJSONObjectValue;
import com.jlbabilino.json.JSONSerializable;
import com.jlbabilino.json.SerializedJSONObjectValue;

@JSONSerializable
public class ValueHolder<T> {

    private T value;

    @DeserializedJSONConstructor
    public ValueHolder(@DeserializedJSONObjectValue(key = "value") T value) {
        setValue(value);
    }

    public ValueHolder() {
        setValue(null);
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SerializedJSONObjectValue(key = "value")
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "value: " + value.toString();
    }
}
