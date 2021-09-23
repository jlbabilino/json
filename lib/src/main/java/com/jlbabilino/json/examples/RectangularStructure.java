// package com.jlbabilino.json.examples;

// import java.util.ArrayList;
// import java.util.List;

// public class RectangularStructure<E> {
//     public static void main(String[] args) {
//         RectangularStructure<Integer> struct = new RectangularStructure<>(4, 2);
//         System.out.println(struct);
//         struct.setRow(1, struct.new RectangularRow(List.of(1, 4, 2, 2)));
//         System.out.println(struct);
//     }

//     public final int length;
//     public final int width;
//     private final List<RectangularRow> list = new ArrayList<>();

//     public RectangularStructure(int length, int width) {
//         this.length = length;
//         this.width = width;
//         for (int row = 0; row < this.width; row++) {
//             list.add(row, new RectangularRow());
//         }
//     }

//     public RectangularStructure(List<RectangularRow> rectangularList) throws NullPointerException, IllegalArgumentException {
//         if (rectangularList == null) {
//             throw new NullPointerException();
//         }
//         if (rectangularList.size() > 0) {
//             length = rectangularList.get(0).size();
//         } else {
//             length = 0;
//         }
//         width = rectangularList.size();
//         for (List<E> rowList : rectangularList) {
//             list.add(new RectangularRow(rowList));
//         }
//     }

//     public void set(int row, int column, E value) throws IndexOutOfBoundsException {
//         if (column <= 0 || column >= length) {
//             throw new IndexOutOfBoundsException();
//         }
//         if (row <= 0 || row >= width) {
//             throw new IndexOutOfBoundsException();
//         }
//         getRow(row).set(column, value);
//     }

//     public E get(int row, int column) throws IndexOutOfBoundsException {
//         if (column <= 0 || column >= length) {
//             throw new IndexOutOfBoundsException();
//         }
//         if (row <= 0 || row >= width) {
//             throw new IndexOutOfBoundsException();
//         }
//         return getRow(row).get(column);
//     }

//     public void setRow(int row, RectangularRow rectangularRow) throws NullPointerException, IndexOutOfBoundsException {
//         if (rectangularRow == null) {
//             throw new NullPointerException();
//         }
//         if (row <= 0 || row >= width) {
//             throw new IndexOutOfBoundsException();
//         }
//         list.set(row, rectangularRow);
//     }

//     public RectangularRow getRow(int row) throws IndexOutOfBoundsException {
//         if (row <= 0 || row >= width) {
//             throw new IndexOutOfBoundsException();
//         }
//         return list.get(row);
//     }

//     @Override
//     public String toString() {
//         return list.toString();
//     }

//     public class RectangularRow {
//         private final List<E> subList = new ArrayList<>();

//         public RectangularRow() {
//             for (int column = 0; column < length; column++) {
//                 subList.add(null);
//             }
//         }

//         public RectangularRow(List<E> rowList) throws NullPointerException, IllegalArgumentException {
//             if (rowList == null) {
//                 throw new NullPointerException();
//             }
//             if (rowList.size() != length) {
//                 throw new IllegalArgumentException();
//             }
//             subList.addAll(rowList);
//         }

//         public void set(int column, E value) throws IndexOutOfBoundsException {
//             if (column <= 0 || column >= length) {
//                 throw new IndexOutOfBoundsException();
//             }
//             subList.set(column, value);
//         }

//         public E get(int column) throws IndexOutOfBoundsException {
//             if (column <= 0 || column >= length) {
//                 throw new IndexOutOfBoundsException();
//             }
//             return subList.get(column);
//         }

//         @Override
//         public String toString() {
//             return subList.toString();
//         }
//     }
// }
