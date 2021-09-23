// package com.jlbabilino.json.examples.generic;

// import com.team2363.lib.file.TextFileReader;
// import com.team2363.lib.json.DeserializedGenericType;
// import com.team2363.lib.json.DeserializedType;
// import com.team2363.lib.json.JSON;
// import com.team2363.lib.json.JSONDeserializer;
// import com.team2363.lib.json.JSONParser;
// import com.team2363.lib.json.JSONSerializer;

// public class GenericDeserializationExample {
//     public static void main(String[] args) throws Exception {
//         String jsonString = new TextFileReader("src\\main\\java\\com\\team2363\\lib\\json\\examples\\animals.json").read();
//         JSON json = JSONParser.parseString(jsonString);
//         DeserializedType type = new DeserializedGenericType(PetSitter.class, Animal.class);
//         Object deserializedObject = JSONDeserializer.deserializeJSON(json, type);
//         @SuppressWarnings("unchecked")
//         PetSitter<Animal> sitter = (PetSitter<Animal>) deserializedObject;
//         System.out.println(sitter);
//         System.out.println(JSONSerializer.serializeJSON(sitter));
        

//     }
// }
