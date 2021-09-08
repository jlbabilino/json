// package com.jlbabilino.json.examples.maptests;

// import java.util.List;

// import com.jlbabilino.json.DeserializedJSONObjectValue;
// import com.jlbabilino.json.JSON;
// import com.jlbabilino.json.JSONDeserializer;
// import com.jlbabilino.json.JSONParser;
// import com.jlbabilino.json.JSONSerializer;
// import com.jlbabilino.json.TypeMarker;

// public class HolderTests {
//     public void setValue(@DeserializedJSONObjectValue(key = "test") ValueHolder<String> test) {

//     }
//     public static void main(String[] args) throws Exception {
//         JSON json = JSONParser.parseString("{\"value\": {\"value\": [[12, .4, -3], [1, 23], [0.2, 3], [40]]}}");
//         TypeMarker<ValueHolder<ValueHolder<List<List<Number>>>>> type = new TypeMarker<>() {};
//         ValueHolder<ValueHolder<List<List<Number>>>> deserialized = JSONDeserializer.deserializeJSON(json, type);
//         System.out.println(deserialized.getValue().getValue().get(1).get(1));
//         System.out.println(JSONSerializer.serializeJSON(deserialized));
//     }
// }
