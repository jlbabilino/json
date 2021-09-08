package com.jlbabilino.json;

import static com.jlbabilino.json.JSONNull.NULL;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

public class JSONSerializer {
    public static JSON serializeJSON(Object obj) {
        return new JSON(serializeJSONEntry(obj));
    }

    /**
     * 
     * @param obj
     * @return
     */
    public static JSONEntry serializeJSONEntry(Object obj) {
        JSONEntry entry;
        if (obj == null) {
            entry = NULL;
        } else {
            Class<?> cls = obj.getClass();
            if (cls.isAnnotationPresent(JSONSerializable.class)) {
                Field[] fields = cls.getFields(); // no need to check for accessiblity since using these methods
                Method[] methods = cls.getMethods();
                switch (cls.getAnnotation(JSONSerializable.class).rootType()) {
                    case OBJECT:
                        Map<String, JSONEntry> objectMap = new HashMap<>();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(SerializedJSONObjectValue.class)) {
                                try {
                                    objectMap.put(field.getAnnotation(SerializedJSONObjectValue.class).key(), serializeJSONEntry(field.get(obj)));
                                } catch (IllegalAccessException e) { // this will never occur because it was checked for
                                }
                            }
                        }
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(SerializedJSONObjectValue.class) && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                try {
                                    objectMap.put(method.getAnnotation(SerializedJSONObjectValue.class).key(), serializeJSONEntry(method.invoke(obj)));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            }
                        }
                        entry = new JSONObject(objectMap);
                        break;
                    case ARRAY:
                        Map<Integer, JSONEntry> arrayMap = new HashMap<>();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(SerializedJSONArrayItem.class)) {
                                try {
                                    arrayMap.put(field.getAnnotation(SerializedJSONArrayItem.class).index(), serializeJSONEntry(field.get(obj)));
                                } catch (IllegalAccessException e) {
                                }
                            }
                        }
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(SerializedJSONArrayItem.class) && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                try {
                                    arrayMap.put(method.getAnnotation(SerializedJSONArrayItem.class).index(), serializeJSONEntry(method.invoke(obj)));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                }
                            }
                        }
                        int largestIndex = 0;
                        for (int key : arrayMap.keySet()) {
                            if (key > largestIndex) { // find largest index user has placed on any annotated field or getter method
                                largestIndex = key;
                            }
                        }
                        JSONEntry[] arrayEntries = new JSONEntry[largestIndex + 1];
                        for (int i = 0; i <= largestIndex; i++) {
                            JSONEntry arrayMapEntry = arrayMap.get(i);
                            if (arrayMapEntry == null) {
                                arrayEntries[i] = NULL;
                            } else {
                                arrayEntries[i] = arrayMapEntry;
                            }
                        }
                        entry = new JSONArray(arrayEntries);
                        break;

                    case BOOLEAN:
                        search: {
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONBoolean.class)) {
                                    try {
                                        Object fieldValue = field.get(obj);
                                        if (fieldValue != null) {
                                            entry = new JSONBoolean((boolean) fieldValue);
                                            break search; // literally, break out of the search because it's been found
                                        }
                                    } catch (IllegalAccessException | ClassCastException e) {
                                        // code only reaches this point if the field wasn't actually a boolean
                                        // that's bad on the developers part and should be checked for but I'm
                                        // not going to worry about exceptions because the idea is that this
                                        // method just works no matter what you throw at it. That is in contrast
                                        // to the deserializer which can have many errors thrown while using it.
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(SerializedJSONBoolean.class) && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                    try {
                                        Object methodResult = method.invoke(obj);
                                        if (methodResult != null) {
                                            entry = new JSONBoolean((boolean) methodResult);
                                            break search;
                                        }
                                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                                    }
                                }
                            }
                            entry = NULL; // if a programmer messed up THAT badly lol
                        }
                    break;

                    case NUMBER:
                        search: {
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONNumber.class)) {
                                    try {
                                        Object fieldValue = field.get(obj);
                                        if (fieldValue != null) {
                                            entry = new JSONNumber((Number) fieldValue);
                                            break search;
                                        }
                                    } catch (IllegalAccessException | ClassCastException e) {
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(SerializedJSONNumber.class) && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                    try {
                                        Object methodResult = method.invoke(obj);
                                        if (methodResult != null) {
                                            entry = new JSONNumber((Number) methodResult);
                                            break search;
                                        }
                                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                                    }
                                }
                            }
                            entry = NULL;
                        }
                    break;

                    case STRING:
                        search: {
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(SerializedJSONString.class)) {
                                    try {
                                        Object fieldValue = field.get(obj);
                                        if (fieldValue != null) {
                                            entry = new JSONString(fieldValue.toString());
                                            break search;
                                        }
                                    } catch (IllegalAccessException | ClassCastException e) {
                                    }
                                }
                            }
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(SerializedJSONString.class) && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
                                    try {
                                        Object methodResult = method.invoke(obj);
                                        if (methodResult != null) {
                                            entry = new JSONString(methodResult.toString());
                                            break search;
                                        }
                                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                                    }
                                }
                            }
                            entry = NULL;
                        }
                    break;

                    default:
                        entry = NULL;
                    break;
                }
            } else if (obj instanceof JSONEntry) {
                entry = (JSONEntry) obj;
            } else if (obj instanceof Collection<?>) {
                Collection<?> objCollection = (Collection<?>) obj;
                int collectionSize = objCollection.size();
                JSONEntry[] collectionEntryArray = new JSONEntry[collectionSize];
                int index = 0;
                for (Object objInCollection : objCollection) {
                    collectionEntryArray[index] = serializeJSONEntry(objInCollection);
                    index++;
                }
                entry = new JSONArray(collectionEntryArray);
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
            } else if (obj instanceof String) { // if unable to do anything else just treat it like a string. Strings will also go to this.
                entry = new JSONString((String) obj);
            } else {
                Method[] methods = cls.getMethods();
                Map<String, JSONEntry> objectMap = new HashMap<>();
                search: {
                    for (Method method : methods) {
                        String methodName = method.getName();
                        if (method.getDeclaringClass() != Object.class && methodName.startsWith("get") && method.getParameterCount() == 0 && method.getReturnType() != void.class) {
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
}
