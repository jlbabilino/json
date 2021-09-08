// package com.jlbabilino.json.examples;

// import java.lang.reflect.*;

// public class TestNested<A> {
//     public SubClass test;
//     public static void main(String[] args) throws Exception {
//         TestNested.StaticClass test = new TestNested.StaticClass();
//         TestNested<String> test = new TestNested<>();
//         System.out.println(test.getLol("lol").subField);
//         Class<?> clazz = TestNested.class;
//         Field field = clazz.getDeclaredField("test");
//         System.out.println(field.getGenericType().getClass());
//         ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
//         System.out.println(parameterizedType.getOwnerType());
//     }
//     public SubClass getLol(A value) {
//         return new SubClass(value);
//     }
//     public class SubClass {
//         public SubClass(A value) {
//             subField = value;
//         }
//         public A subField;
//     }
//     public static class StaticClass {
//     }
// }