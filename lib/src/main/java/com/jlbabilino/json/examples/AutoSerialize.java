package com.jlbabilino.json.examples;

import com.jlbabilino.json.JSON;
import com.jlbabilino.json.JSONSerializer;

public class AutoSerialize {
    public static void main(String[] args) {
        AutoSerialize as = new AutoSerialize();
        JSON json = JSONSerializer.serializeJSON(as);
        System.out.println(json);
    }

    private boolean property0 = true;
    private int property1 = 21;
    private String[] property2 = {"12", "sdlfkja", "sugma"};
    public AutoSerialize() {
    }
    public boolean getProperty0() {
        return property0;
    }
    public int getProperty1() {
        return property1;
    }
    public String[] getProperty2() {
        return property2;
    }
}
