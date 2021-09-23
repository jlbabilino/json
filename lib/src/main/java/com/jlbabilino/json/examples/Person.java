package com.jlbabilino.json.examples;

import com.jlbabilino.json.DeserializedJSONConstructor;
import com.jlbabilino.json.DeserializedJSONObjectValue;
import com.jlbabilino.json.DeserializedJSONTarget;
import com.jlbabilino.json.JSON;
import com.jlbabilino.json.JSONDeserializer;
import com.jlbabilino.json.JSONSerializable;
import com.jlbabilino.json.JSONSerializer;
import com.jlbabilino.json.SerializedJSONObjectValue;

@JSONSerializable
public class Person {
    public static void main(String[] args) throws Exception {
        Person person = new Person("Joe George", 32);
        System.out.println("Printing person:");
        System.out.println(person);
        JSON json = JSONSerializer.serializeJSON(person);
        System.out.println("Printing serialized json:");
        System.out.println(json);
        Person samePerson = JSONDeserializer.deserializeJSON(json, Person.class);
        System.out.println("Printing same person:");
        System.out.println(samePerson);

    }
    private String name;
    private int age;

    public Person(String name, int age) {
        setName(name);
        setAge(age);
    }

    @DeserializedJSONConstructor
    public Person() {
        this("", 0);
    }

    @DeserializedJSONTarget
    public void setName(@DeserializedJSONObjectValue(key = "name") String name) {
        this.name = name;
    }

    @SerializedJSONObjectValue(key = "name")
    public String getName() {
        return name;
    }

    @DeserializedJSONTarget
    public void setAge(@DeserializedJSONObjectValue(key = "age") int age) {
        this.age = age;
    }

    @SerializedJSONObjectValue(key = "age")
    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "name: " + name + ", age: " + age;
    }
}
