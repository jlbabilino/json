// package com.jlbabilino.json.examples;

// import com.jlbabilino.json.JSON;
// import com.jlbabilino.json.JSONDeserializer;
// import com.jlbabilino.json.JSONParser;
// import com.jlbabilino.json.TypeMarker;

// import java.io.File;
// import java.lang.reflect.Type;
// import java.net.URL;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.util.Arrays;
// import java.util.List;
// import java.net.URLClassLoader;

// public class TestA {
//     public static void main(String[] args) throws Exception {
//         TypeMarker<> type = new TypeMarker<>(){};
//         String str = "[[0, 1, 2, 3, 4, 5, 6, 7, 8, 9],[1, 2, 3, 4, 5, 6, 7, 8, 9, 0],[2, 3, 4, 5, 6, 7, 8, 9, 0, 1],[3, 4, 5, 6, 7, 8, 9, 0, 1, 2],[4, 5, 6, 7, 8, 9, 0, 1, 2, 3],[5, 6, 7, 8, 9, 0, 1, 2, 3, 4],[6, 7, 8, 9, 0, 1, 2, 3, 4, 5],[7, 8, 9, 0, 1, 2, 3, 4, 5, 6],[8, 9, 0, 1, 2, 3, 4, 5, 6, 7],[9, 0, 1, 2, 3, 4, 5, 6, 7, 8]]";
//         JSON json = JSONParser.parseString(str);
//         List<Integer>[] deserialized = JSONDeserializer.deserializeJSON(json, type);
//         System.out.println(deserialized);
//         // for (int[] array : deserialized) {
//         //     System.out.println(Arrays.toString(array));
//         // }
//     }
// }
