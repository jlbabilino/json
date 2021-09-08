// package com.jlbabilino.json.examples.generic;

// import java.util.List;

// import com.jlbabilino.json.DeserializedJSONObjectValue;
// import com.jlbabilino.json.DeserializedJSONTarget;
// import com.jlbabilino.json.JSONSerializable;
// import com.jlbabilino.json.SerializedJSONObjectValue;

// @JSONSerializable
// public class PetSitter<A extends Animal> {

//     private final String name;
//     private final int skill;
//     private List<A> petsSitting;

//     @DeserializedJSONTarget
//     public PetSitter(@DeserializedJSONObjectValue(key = "name") String name, @DeserializedJSONObjectValue(key = "skill") int skill) {
//         this.name = name;
//         this.skill = skill;

//     }
//     @DeserializedJSONTarget
//     public void setPetsSitting(@DeserializedJSONObjectValue(key = "pets_sitting") List<A> petsSitting) {
//         this.petsSitting = petsSitting;
//     }
//     @SerializedJSONObjectValue(key = "pets_sitting")
//     public List<A> getPetsSitting() {
//         return petsSitting;
//     }

//     @SerializedJSONObjectValue(key = "name")
//     public String getName() {
//         return name;
//     }
    
//     @SerializedJSONObjectValue(key = "skill")
//     public int getSkill() {
//         return skill;
//     }

//     @Override
//     public String toString() {
//         return name + ", " + skill + ", " + petsSitting.toString();
//     }
// }
