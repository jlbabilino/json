package com.jlbabilino.json;

import static com.jlbabilino.json.JSONAnnotations.getJSONAnnotation;
import static com.jlbabilino.json.JSONAnnotations.isJSONAnnotationPresent;
import static com.jlbabilino.json.JSONNull.NULL;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jlbabilino.json.JSONEntry.JSONType;

/**
 * <p>
 * This class takes Java object inputs and converts them to JSON. This is useful
 * for applications that need to save an object to a JSON file. JSON
 * deserialization can be used to convert this JSON data back to Java objects.
 * </p>
 * <p>
 * A Java object may be serialized in several ways:
 * </p>
 * <ol>
 * <li><b>Null serialization</b>: If the object is {@code null}, it will be
 * serialized as {@code JSONNull.NULL}
 * <li><b>Custom serialization</b>: You can annotate your class with
 * {@link JSONSerializable} to mark it for custom serilaization. This method
 * allows you to control what fields and methods are serialized and what type of
 * {@link JSONEntry} the object is serilaized to.
 * <li><b>JSON serialization</b>: If the object is an instance of one of the
 * {@code JSONEntry} subclasses, then the serialized object will the object put
 * in. If the object is an instance of {@link JSON}, then it may be serialized
 * as a {@code JSONEntry} or a {@code JSON} depending on which serialization
 * method is used.
 * <li><b>Collection serialization</b>: Java Collections will be serialized as
 * JSON arrays by iterating over the entire collection then serializing each
 * item and placing it in the JSON array.
 * <li><b>Map serilaization</b>: Java Maps will be serialized as JSON objects.
 * Each entry in the JSON object is collected by iterating over the map, then on
 * each item, invoking {@code toString()} on the key to be used as the key in
 * the JSON object, and serializing the value to be used for the value in the
 * JSON object.
 * <li><b>Enum serialization</b>: Java enums will be serialized to JSON strings
 * of the same value as their enumeration name.
 * <li><b>Array serialization</b>: Arrays will be serialized to JSON arrays,
 * with each value collected through iteration on the array, then on each item,
 * serializing the item to be placed in the JSON array. Ordering will always be
 * preserved.
 * <li><b>Primative/Wrapper serialization</b>: Java primatives may be serialized
 * to different JSON entry types depending on the type:
 * <ul>
 * <li>Java {@code Boolean} objects (or primatives) are converted to
 * {@code JSONBoolean} types.
 * <li>Java {@code Number} objects (or primatives) are converted to
 * {@code JSONNumber} types.
 * <li>Java {@code Character} objects (or primatives) are converted to
 * {@code JSONString} types with strings of length 1 containing the character.
 * </ul>
 * <li><b>String serialization</b>: Java strings will be serialized to
 * {@code JSONString} strings, completely preserving data.
 * </ol>
 * <p>
 * If the serilializer is unable to use any of the above methods, it will first
 * attempt to automatically serialize the object. This is a good method for
 * simple Java classes. Automatic serialization always serializes to JSON
 * objects. It looks for public methods that have a name with prefix of "get",
 * return anything but {@code void}, and have zero parameters. It will then get
 * the substring after "get" to use for the key in the JSON object entry, then
 * it will invoke the method and serialize the result for the value.
 * </p>
 * <p>
 * Custom serialization is the core of the second main part of this library,
 * JSON serialization. As previously mentioned, you must mark a class with
 * {@code JSONSerializable} to indicate that the serializer should do custom
 * serialization. The annotation has one property, {@code rootType}, which
 * indicates the JSON entry type you want the object to be serialized to. This
 * property defaults to {@code JSONEntry.JSONType.OBJECT}.
 * </p>
 * <p>
 * For all JSON entry types, a single {@code SerializedJSONEntry} annotation can
 * be used to indicate that a public field or method should be serialized and
 * used as the serialization of the object. These public fields or methods must
 * be accessible, and the methods must have zero parameters and not return
 * {@code void.class}. This annotation is particularly helpful with objects and
 * arrays where the size, indicies, and keys are specific to the Java object.
 * Basically, it allows you to make variable length JSON arrays and dynamic JSON
 * objects.
 * </p>
 * <p>
 * For classes that serialize to JSON objects, multiple fields and methods (of
 * the same requirements) can be annotated with
 * {@code SerializedJSONObjectValue} to indicate that they should be serialized
 * and placed in the new JSON object as a key-value pair.
 * {@code SerializedJSONObjectValue} has one property, {@code key}, which is the
 * {@code String} that should be used as the key in the key-value pair. The
 * value of the field or result of the method will be serialized and used as the
 * value.
 * </p>
 * <p>
 * Similarly, for classes that serialize to JSON arrays, multiple fields and
 * methods (of the same requirements) can be annotated with
 * {@code SerializedJSONArrayItem} to indicate that they should be serialized
 * and placed in the new JSON array as an array item.
 * {@code SerializedJSONArrayItem} has one property, {@code index}, which is the
 * {@code int} value that should be used as the index for the array item. The
 * value of the field or result of the method will be serialized and placed in
 * the new array at the index specified. The size of the new JSON array is equal
 * to the largest index specified plus one, and any spaces not filled in the
 * middle will be filled with JSON nulls.
 * </p>
 * <p>
 * Due to the recursive nature of JSON, serialization is also recursive. The
 * methods that serialize Java objects are used recursively to serialize the
 * fields of the object.
 * </p>
 * 
 * @see JSONDeserializer
 * @author Justin Babilino
 */
public final class JSONSerializer {

    /**
     * Prevent instantiation
     */
    private JSONSerializer() {
    }

    /**
     * Serializes a Java object to a {@link JSON}.
     * 
     * @param obj the object to serialize
     * @return the serialized {@code JSON}
     */
    public static JSON serializeJSON(Object obj) {
        return new JSON(serializeJSONEntry(obj));
    }

    /**
     * Serializes a Java object to a {@link JSONEntry}.
     * 
     * @param obj the object to serialize
     * @return the serialized {@code JSONEntry}
     */
    public static JSONEntry serializeJSONEntry(Object obj) {
        JSONEntry entry;
        if (obj == null) {
            entry = NULL;
        } else {
            Class<?> cls = obj.getClass();
            if (cls.isAnnotationPresent(JSONSerializable.class)) {
                JSONType jsonType = cls.getAnnotation(JSONSerializable.class).rootType();
                if (jsonType == JSONType.NULL) {
                    entry = NULL; // just why would you EVER do this...
                } else {
                    Field[] fields = cls.getFields(); // no need to check for accessiblity since using these methods
                    Method[] methods = cls.getMethods();
                    switch (jsonType) {
                    case OBJECT:
                        objectMode: {
                            Map<String, JSONEntry> objectMap = new HashMap<>();
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONEntry.class)) {
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(field.get(obj));
                                        if (entryTest.getType() == jsonType) {
                                            entry = entryTest;
                                            break objectMode;
                                        }
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                                if (field.isAnnotationPresent(SerializedJSONObjectValue.class)) {
                                    String key = field.getAnnotation(SerializedJSONObjectValue.class).key();
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(field.get(obj));
                                        objectMap.put(key, entryTest);
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                    if (isJSONAnnotationPresent(SerializedJSONEntry.class, method)) {
                                        try {
                                            JSONEntry entryTest = serializeJSONEntry(method.invoke(obj));
                                            if (entryTest.getType() == jsonType) {
                                                entry = entryTest;
                                                break objectMode;
                                            }
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                        }
                                    }
                                    if (isJSONAnnotationPresent(SerializedJSONObjectValue.class, method)) {
                                        String key = getJSONAnnotation(SerializedJSONObjectValue.class, method).key();
                                        try {
                                            JSONEntry entryTest = serializeJSONEntry(method.invoke(obj));
                                            objectMap.put(key, entryTest);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                        }
                                    }
                                }
                            }
                            entry = new JSONObject(objectMap);
                        }
                        break;
                    case ARRAY:
                        arrayMode: {
                            Map<Integer, JSONEntry> arrayMap = new HashMap<>();
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONEntry.class)) {
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(field.get(obj));
                                        if (entryTest.getType() == jsonType) {
                                            entry = entryTest;
                                            break arrayMode;
                                        }
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                                if (field.isAnnotationPresent(SerializedJSONArrayItem.class)) {
                                    int index = field.getAnnotation(SerializedJSONArrayItem.class).index();
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(field.get(obj));
                                        arrayMap.put(index, entryTest);
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                    if (isJSONAnnotationPresent(SerializedJSONEntry.class, method)) {
                                        try {
                                            JSONEntry entryTest = serializeJSONEntry(method.invoke(obj));
                                            if (entryTest.getType() == jsonType) {
                                                entry = entryTest;
                                                break arrayMode;
                                            }
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                        }
                                    }
                                    if (isJSONAnnotationPresent(SerializedJSONArrayItem.class, method)) {
                                        int index = getJSONAnnotation(SerializedJSONArrayItem.class, method).index();
                                        try {
                                            JSONEntry entryTest = serializeJSONEntry(method.invoke(obj));
                                            arrayMap.put(index, entryTest);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                        }
                                    }
                                }
                            }
                            int largestIndex = 0;
                            for (int key : arrayMap.keySet()) {
                                if (key > largestIndex) { // find largest index user has placed on any
                                                          // annotated field or getter method
                                    largestIndex = key;
                                }
                            }
                            JSONEntry[] arrayEntries = new JSONEntry[largestIndex + 1];
                            for (int i = 0; i <= largestIndex; i++) {
                                if (!arrayMap.containsKey(i)) {
                                    arrayEntries[i] = NULL;
                                } else {
                                    arrayEntries[i] = arrayMap.get(i);
                                }
                            }
                            entry = new JSONArray(arrayEntries);
                        }
                        break;
                    default:
                        entryMode: {
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONEntry.class)) {
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(field.get(obj));
                                        if (entryTest.getType() == jsonType) {
                                            entry = entryTest;
                                            break entryMode;
                                        }
                                    } catch (IllegalAccessException e) {
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.getParameterCount() == 0 && method.getReturnType() != void.class
                                        && isJSONAnnotationPresent(SerializedJSONEntry.class, method)) {
                                    try {
                                        JSONEntry entryTest = serializeJSONEntry(method.invoke(obj));
                                        if (entryTest.getType() == jsonType) {
                                            entry = entryTest;
                                            break entryMode;
                                        }
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                    }
                                }
                            }
                            entry = NULL;
                        }
                        break;
                    }
                }
            } else if (obj instanceof JSONEntry) {
                entry = (JSONEntry) obj;
            } else if (obj instanceof JSON) {
                entry = ((JSON) obj).getRoot();
            } else if (obj instanceof Collection<?>) {
                @SuppressWarnings("unchecked")
                Collection<Object> objCollection = (Collection<Object>) obj;
                int collectionSize = objCollection.size();
                JSONEntry[] collectionEntryArray = new JSONEntry[collectionSize];
                int index = 0;
                for (Object objInCollection : objCollection) {
                    collectionEntryArray[index] = serializeJSONEntry(objInCollection);
                    index++;
                }
                entry = new JSONArray(collectionEntryArray);
            } else if (obj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> objMap = (Map<Object, Object>) obj;
                Map<String, JSONEntry> objectMap = new HashMap<>();
                for (Map.Entry<Object, Object> objMapEntry : objMap.entrySet()) {
                    Object objMapKey = objMapEntry.getKey();
                    Object objMapValue = objMapEntry.getValue();
                    if (objMapKey != null && objMapValue != null) {
                        String key = objMapEntry.toString();
                        JSONEntry value = serializeJSONEntry(objMapValue);
                        objectMap.put(key, value);
                    }
                }
                entry = new JSONObject(objectMap);
            } else if (cls.isEnum()) {
                entry = new JSONString(obj.toString());
            } else if (cls.isArray()) {
                int arrayLength = Array.getLength(obj);
                JSONEntry[] arrayArray = new JSONEntry[arrayLength];
                for (int i = 0; i < arrayLength; i++) {
                    arrayArray[i] = serializeJSONEntry(Array.get(obj, i));
                }
                entry = new JSONArray(arrayArray);
            } else if (obj instanceof Boolean) {
                entry = new JSONBoolean((boolean) obj);
            } else if (obj instanceof Number) {
                entry = new JSONNumber((Number) obj);
            } else if (obj instanceof Character) {
                entry = new JSONString(Character.toString((Character) obj));
            } else if (obj instanceof String) {
                entry = new JSONString((String) obj);
            } else { // automatic serialization
                Method[] methods = cls.getMethods();
                Map<String, JSONEntry> objectMap = new HashMap<>();
                search: {
                    for (Method method : methods) {
                        String methodName = method.getName();
                        if (method.getDeclaringClass() != Object.class && methodName.startsWith("get")
                                && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                            try {
                                Object methodReturnValue = method.invoke(obj);
                                if (methodName.length() == 3) {
                                    entry = serializeJSONEntry(methodReturnValue);
                                    break search;
                                } else {
                                    String propertyName = methodName.substring(3);
                                    objectMap.put(propertyName, serializeJSONEntry(methodReturnValue));
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                            }
                        }
                    }
                    entry = new JSONObject(objectMap);
                }
            }
        }
        return entry;
    }
    
    /**
     * Serializes a Java object to a string; this method can be used as an implementation
     * of the {@code toString()} method for {@code JSONSerializable} classes. It also is
     * useful for debugging. For example, Java does not have a {@code toString()} method
     * for arrays, but an array could be fed into this method to list its contents.
     * 
     * @param obj the object to serialize
     * @return the serialized JSON string
     */
    public static String serializeString(Object obj) {
        return serializeJSON(obj).exportJSON();
    }

    /**
     * Serializes a Java object to a string, then writes the string to the file specified.
     * 
     * @param obj the object to serialize
     * @param file the file to write the serialized data to
     * @throws NullPointerException if the file is null
     * @throws IOException if there is an error while writing the file
     */
    public static void serializeFile(Object obj, File file) throws NullPointerException, IOException {
        if (file == null) {
            throw new NullPointerException("File is null.");
        }
        file.mkdirs();
        file.createNewFile();
        if (file.canWrite()) {
            String str = serializeString(obj);
            PrintWriter writer = new PrintWriter(file);
            writer.print(str);
            writer.close();
        } else {
            throw new IOException("Cannot write to file: " + file.getAbsolutePath());
        }
    }
}
