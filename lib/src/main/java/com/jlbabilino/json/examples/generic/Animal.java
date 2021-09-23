// package com.jlbabilino.json.examples.generic;

// import com.jlbabilino.json.DeserializedJSONConstructor;
// import com.jlbabilino.json.DeserializedJSONDeterminer;
// import com.jlbabilino.json.DeserializedJSONObjectValue;
// import com.jlbabilino.json.JSONDeserializerException;
// import com.jlbabilino.json.JSONEntry;
// import com.jlbabilino.json.JSONSerializable;
// import com.jlbabilino.json.SerializedJSONObjectValue;
// import com.jlbabilino.json.TypeMarker;

// @JSONSerializable
// public abstract class Animal {

//     private static TypeMarker<Cat> catTypeMarker = new TypeMarker<>() {};
//     private static TypeMarker<Dog> dogTypeMarker = new TypeMarker<>() {};

//     private final int age;

//     public Animal(int age) {
//         this.age = age;
//     }

//     public abstract String getSpecies();

//     @SerializedJSONObjectValue(key = "age")
//     public int getAge() {
//         return age;
//     }

//     @DeserializedJSONDeterminer
//     public static TypeMarker<? extends Animal> determiner(JSONEntry jsonEntry) throws JSONDeserializerException {
//         if (jsonEntry != null && jsonEntry.isObject() && jsonEntry.containsKey("species") && jsonEntry.getKeyedEntry("species").isString()) {
//             switch (jsonEntry.getKeyedEntry("species").getString()) {
//                 case "Dog":
//                     return dogTypeMarker;
//                 case "Cat":
//                     return catTypeMarker;
//                 default:
//                     return new TypeMarker<Animal>(){
                        
//                     };
//             }
//         } else {
//             throw new JSONDeserializerException("Unable to select appropriate Animal for deserialization ");
//         }
        
//     }

//     public String toString() {
//         return getSpecies();
//     }
// }
