// package com.jlbabilino.json.examples;

// import java.lang.reflect.ParameterizedType;
// import java.lang.reflect.Type;
// import java.util.List;
// import java.util.Map;

// public interface Example<T> {
//     public static void main(String[] args) {
//         Example<Map<List<? extends Integer>, String>> test = new Example<>() {};
//         Example<? extends Object> lol = new Example<>() {};
//         Class<?> clazz = test.getClass();
//         Type[] interfaces = clazz.getGenericInterfaces();
//         ParameterizedType example = (ParameterizedType) interfaces[0];
//         Type arg = example.getActualTypeArguments()[0];
//         System.out.println(arg);
//     }
// }
