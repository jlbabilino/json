// package com.jlbabilino.json.examples.arrayandlist;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;

// import com.jlbabilino.json.JSON;
// import com.jlbabilino.json.JSONDeserializer;
// import com.jlbabilino.json.JSONDeserializerException;
// import com.jlbabilino.json.JSONParser;
// import com.jlbabilino.json.JSONParserException;
// import com.jlbabilino.json.TypeMarker;

// public class ListDeserializationExample {
//     public static void main(String[] args) throws IOException {
//         String jsonString = "[[0,1,2,3,4,5,6,7,8,9],[1,2,3,4,5,6,7,8,9,0],[2,3,4,5,6,7,8,9,0,1],[3,4,5,6,7,8,9,0,1,2],[4,5,6,7,8,9,0,1,2,3],[5,6,7,8,9,0,1,2,3,4],[6,7,8,9,0,1,2,3,4,5],[7,8,9,0,1,2,3,4,5,6],[8,9,0,1,2,3,4,5,6,7],[9,0,1,2,3,4,5,6,7,8]]";
//         try {
//             JSON json = JSONParser.parseString(jsonString);
//             TypeMarker<List<int[]>> typeMarker = new TypeMarker<>() {};
//             List<int[]> list = JSONDeserializer.deserializeJSON(json, typeMarker);
//             for (int[] arry : list) {
//                 System.out.print(Arrays.toString(arry));
//                 System.out.println(",");
//             }
//         } catch (JSONParserException | JSONDeserializerException e) {
//             System.out.println(e);
//         }


//     }

// }
