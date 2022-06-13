/*
 * Copyright (C) 2021 Justin Babilino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jlbabilino.json;

import static com.jlbabilino.json.JSONAnnotations.isJSONAnnotationPresent;
import static com.jlbabilino.json.ResolvedTypes.isResolved;
import static com.jlbabilino.json.ResolvedTypes.resolveClass;
import static com.jlbabilino.json.ResolvedTypes.resolveType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jlbabilino.json.JSONEntry.JSONType;

/**
 * This class is used to deserialize JSON into Java objects. It uses annotations
 * and reflection to instantiate objects and populate them with the appropriate
 * data. The main static methods each take a JSON and a Java type, which are
 * then used to deserialize the JSON data to the appropriate Java object. The
 * Java type must point to a Java type that this system is compatible with. This
 * includes most Java Collections and all primatives and wrapper classes. You
 * can create your own compatible objects by annotating a class with the
 * DeserializedJSON annotation family (see {@link JSONSerializable}).
 * 
 * @see JSONSerializer
 * @author Justin Babilino
 */
public class JSONDeserializer {

    /**
     * This holds the type parameter {@code E} in the {@link List} class for use in
     * the deserializer.
     */
    private static final TypeVariable<?> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];

    /**
     * Deserializes a {@link JSON} to a Java type specified by a given
     * {@link TypeMarker}. Returns an object of the same type as the
     * {@code TypeMarker}.
     * 
     * @param <T>        the Java type that the JSON is being deserialized to; it
     *                   must agree with the TypeMarker
     * @param json       the JSON data to convert to a Java object
     * @param typeMarker the type of Java object to create
     * @return the newly created and populated Java object of type {@code <T>}
     * @throws NullPointerException      if the JSON or type marker is {@code null}
     * @throws IllegalArgumentException  if the {@code TypeMarker} is not fully
     *                                   resolved (if it contains type variables)
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(JSON json, TypeMarker<T> typeMarker)
            throws NullPointerException, IllegalArgumentException, JSONDeserializerException {
        return deserialize(json.getRoot(), typeMarker);
    }

    /**
     * Deserializes a {@link JSON} to a Java type specified by a given
     * {@link Class}. Returns an object of the same type as the {@code Class}.
     * 
     * @param <T>       the Java type that the JSON is being deserialized to; it
     *                  must agree with the {@code Class}
     * @param json      the JSON data to convert to a Java object
     * @param typeClass the type of Java object to create
     * @return the newly created and populated Java object of type {@code <T>}
     * @throws NullPointerException      if the JSON or type class is {@code null}
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(JSON json, Class<T> typeClass)
            throws NullPointerException, JSONDeserializerException {
        return deserialize(json.getRoot(), typeClass);
    }

    /**
     * The main internal method for deserialization, all other methods reduce to a
     * call of this. This method accepts a {@link JSONEntry} and a Java reflection
     * {@link Type}, which contains all type information for the creation of the
     * Java object. This library has private implemenations of the subtypes of Java
     * reflection Types (e.g. {@link ParameterizedType}), which may be used with
     * this method.
     * 
     * @param jsonEntry        the JSON entry to be converted to a Java object
     * @param deserializedType the Java type to deserialize to
     * @return the newly created and populated Java object
     * @throws NullPointerException      if the JSON entry or {@code Type} is
     *                                   {@code null}
     * @throws JSONDeserializerException if there is an error while deserializing.
     */
    private static Object deserialize(JSONEntry jsonEntry, Type deserializedType)
            throws NullPointerException, JSONDeserializerException {
        if (jsonEntry == null) {
            throw new NullPointerException("The JSON entry being deserialized is null");
        }
        // this map is used to resolve type variables in classes. For example, if the
        // Type were a List<Integer>, then the map would have an entry of {E ->
        // Integer.class}.
        Map<TypeVariable<?>, Type> typeVariableMap = new HashMap<>();
        Class<?> baseClass = resolveClass(deserializedType);
        Object deserializedObject;
        if (jsonEntry.isNull()) {
            deserializedObject = null;
        } else {
            if (deserializedType instanceof GenericArrayType) {
                if (jsonEntry.getType() != JSONType.ARRAY) {
                    throw new JSONDeserializerException(
                            "Cannot convert JSON " + jsonEntry.getType() + " to Java array type.");
                }
                JSONEntry[] arrayEntries = ((JSONArray) jsonEntry).getArray();
                int arrayLength = arrayEntries.length;
                // have to get component because otherwise would make array with one too many
                // dimensions:
                Object newArray = Array.newInstance(baseClass.getComponentType(), arrayLength);
                Type genericComponentType = ((GenericArrayType) deserializedType).getGenericComponentType();
                for (int i = 0; i < arrayLength; i++) {
                    Array.set(newArray, i, deserialize(arrayEntries[i], genericComponentType));
                }
                deserializedObject = newArray;
            } else {
                if (deserializedType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) deserializedType;
                    TypeVariable<?>[] classTypeParameters = baseClass.getTypeParameters();
                    Type[] actualTypeParameters = parameterizedType.getActualTypeArguments();
                    for (int i = 0; i < classTypeParameters.length; i++) {
                        typeVariableMap.put(classTypeParameters[i], actualTypeParameters[i]);
                    }
                }
                if (baseClass.isAnnotationPresent(JSONSerializable.class)) {
                    JSONType classJSONType = baseClass.getAnnotation(JSONSerializable.class).rootType();
                    if (jsonEntry.getType() != classJSONType) {
                        throw new JSONDeserializerException(
                                "Could not deserialize because the type of JSON Entry provided does not match the JSON type specified by the class "
                                        + baseClass.getCanonicalName() + ".");
                    }
                    Field[] fields = baseClass.getFields();
                    Method[] methods = baseClass.getMethods();
                    int baseClassModifiers = baseClass.getModifiers();
                    if (baseClass.isInterface() || Modifier.isAbstract(baseClassModifiers)) {
                        search: {
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(DeserializedJSONDeterminer.class)) {
                                    int determinerModifiers = method.getModifiers();
                                    if (!Modifier.isStatic(determinerModifiers)) {
                                        throw new JSONDeserializerException("Determiner " + method.getName() + " in "
                                                + baseClass.getCanonicalName() + " must be a static method.");
                                    }
                                    if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != JSONEntry
                                            .classForJSONType(classJSONType)) {
                                        throw new JSONDeserializerException("Determiner " + method.getName() + " in "
                                                + baseClass.getCanonicalName()
                                                + " must have exactly one parameter of the JSONEntry class associated with the JSONType that this class serializes to. For example, a class that serializes to OBJECT will need to have a parameter of type JSONObject.");
                                    }
                                    if (method.getExceptionTypes().length != 1
                                            || method.getExceptionTypes()[0] != JSONDeserializerException.class) {
                                        throw new JSONDeserializerException("Determiner " + method.getName() + " in "
                                                + baseClass.getCanonicalName()
                                                + " must throw one exception of type JSONDeserializerException");
                                    }
                                    if (method.getReturnType() != TypeMarker.class
                                            || !(method.getGenericReturnType() instanceof ParameterizedType)) {
                                        throw new JSONDeserializerException(
                                                "Determiner " + method.getName() + " in " + baseClass.getCanonicalName()
                                                        + " must return a parameterized TypeMarker<>");
                                    }
                                    ParameterizedType genericReturnType = (ParameterizedType) method
                                            .getGenericReturnType();
                                    if (!(genericReturnType.getActualTypeArguments()[0] instanceof WildcardType)) {
                                        throw new JSONDeserializerException(
                                                "Determiner " + method.getName() + " in " + baseClass.getCanonicalName()
                                                        + " must return a wildcard TypeMarker<?>.");
                                    }
                                    WildcardType typeMarkerWildcard = (WildcardType) genericReturnType
                                            .getActualTypeArguments()[0];
                                    Type wildcardUpperBound = typeMarkerWildcard.getUpperBounds()[0];
                                    if (method.getTypeParameters().length != 0) {
                                        throw new JSONDeserializerException("Determiner " + method.getName() + " in "
                                                + baseClass.getCanonicalName() + " should not be a generic method.");
                                    }
                                    if (resolveClass(wildcardUpperBound) != baseClass) {
                                        throw new JSONDeserializerException("Determiner " + method.getName() + " in "
                                                + baseClass.getCanonicalName()
                                                + "must return an upper bounded wildcard TypeMarker<? extends TYPE>, where TYPE is this interface or abstract class. TYPE may also be parameterized.");
                                    }
                                    if (wildcardUpperBound instanceof ParameterizedType) {
                                        ParameterizedType parameterizedUpperBound = (ParameterizedType) wildcardUpperBound;
                                        Type[] upperBoundTypeArguments = parameterizedUpperBound
                                                .getActualTypeArguments();
                                        for (Type typeArgument : upperBoundTypeArguments) {
                                            if (!(typeArgument instanceof WildcardType)) {
                                                throw new JSONDeserializerException("Determiner " + method.getName()
                                                        + " in " + baseClass.getCanonicalName()
                                                        + " returns a type that extends "
                                                        + wildcardUpperBound.toString()
                                                        + ", but there is a type parameter in the parameterized type that is not a wildcard. All type parameters here should be wildcards.");
                                            }
                                        }
                                    }
                                    // all conditions are met at this point
                                    try {
                                        TypeMarker<?> determinedType = (TypeMarker<?>) method.invoke(null, jsonEntry);
                                        deserializedObject = deserialize(jsonEntry, determinedType);
                                    } catch (IllegalAccessException e) {
                                        // I'm still not quite sure if this is possible. Let me know if you know.
                                        throw new JSONDeserializerException("Could not invoke determiner "
                                                + method.getName() + " in " + baseClass.getCanonicalName() + ".");
                                    } catch (InvocationTargetException e) {
                                        throw new JSONDeserializerException("Unable to determine type from determiner "
                                                + method.getName() + " in " + baseClass.getCanonicalName() + ": "
                                                + e.getCause().getMessage()); // print user-defined message
                                    }
                                    break search;
                                }
                            }
                            // if reaches here, it must have failed
                            throw new JSONDeserializerException("Cannot deserialize to interface or abstract class "
                                    + baseClass.getCanonicalName()
                                    + " because there is no determiner method present in it. For more info about determiners, see the annotation DeserializedJSONDeterminer documentation.");
                        }
                    } else {
                        Constructor<?>[] constructors = baseClass.getConstructors();
                        search: {
                            for (Constructor<?> constructor : constructors) {
                                if (constructor.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                                    try {
                                        deserializedObject = constructor.newInstance(
                                                prepareParameters(jsonEntry, constructor, typeVariableMap));
                                    } catch (IllegalAccessException e) {
                                        throw new JSONDeserializerException("Could not invoke constructor "
                                                + constructor.toString() + " in " + baseClass.getCanonicalName() + ".");
                                    } catch (InvocationTargetException e) {
                                        throw new JSONDeserializerException("Unable to create instance of "
                                                + baseClass.getCanonicalName() + " with constructor "
                                                + constructor.toString() + ": " + e.getCause().getMessage()); // print
                                                                                                              // user-defined
                                                                                                              // message
                                    } catch (Exception e) {
                                        // dead code
                                        throw new JSONDeserializerException("message: " + e.getMessage());
                                    }
                                    break search;
                                }
                            }
                            throw new JSONDeserializerException(
                                    "Unable to find a DeserializedJSONConstructor annotated constructor in class "
                                            + baseClass.getCanonicalName());
                        }
                        for (Field field : fields) {
                            JSONEntry entry = null;
                            if (field.isAnnotationPresent(DeserializedJSONEntry.class)) {
                                entry = jsonEntry;
                            } else if (field.isAnnotationPresent(DeserializedJSONObjectValue.class)) {
                                if (classJSONType != JSONType.OBJECT) {
                                    throw new JSONDeserializerException("Cannot use \"DeserializedJSONObjectValue\" since " +
                                            baseClass.getCanonicalName() + " serializes to JSON " +
                                            classJSONType + ".");
                                }
                                JSONObject jsonObject = (JSONObject) jsonEntry;
                                String key = field.getAnnotation(DeserializedJSONObjectValue.class).key();
                                if (!jsonObject.containsKey(key)) {
                                    throw new JSONDeserializerException("Field " + field.toString() + " in Class "
                                            + baseClass.getCanonicalName() + " requests the value mapped to key \""
                                            + key + "\", but that key is unavailible in the JSON object.");
                                }
                                entry = jsonObject.get(key);
                            } else if (field.isAnnotationPresent(DeserializedJSONArrayItem.class)) {
                                if (classJSONType != JSONType.ARRAY) {
                                    throw new JSONDeserializerException("Cannot use \"DeserializedJSONArrayItem\" since " +
                                            baseClass.getCanonicalName() + " serializes to JSON " +
                                            classJSONType + ".");
                                }
                                JSONArray jsonArray = (JSONArray) jsonEntry;
                                int index = field.getAnnotation(DeserializedJSONArrayItem.class).index();
                                if (index < 0 || index >= jsonArray.length()) {
                                    throw new JSONDeserializerException("Field " + field.toString() + " in Class "
                                            + baseClass.getCanonicalName() + " requests the value at array index "
                                            + index + ", but that index is out of bounds in the JSON array.");
                                }
                                entry = jsonArray.get(index);
                            }
                            if (entry != null) { // if one of the deserialized annotations was used
                                Type resolvedType = resolveType(field.getGenericType(), typeVariableMap);
                                Object newValue = deserialize(entry, resolvedType);
                                try {
                                    field.set(deserializedObject, newValue);
                                } catch (IllegalAccessException e) {
                                    throw new JSONDeserializerException("Unable to set field " + field.getName() +
                                            " in " + baseClass.getCanonicalName() + ".");
                                }
                            }
                        }
                        for (Method method : methods) {
                            if (isJSONAnnotationPresent(DeserializedJSONTarget.class, method)) {
                                Object[] preparedParameters = prepareParameters(jsonEntry, method, typeVariableMap);
                                try {
                                    method.invoke(deserializedObject, preparedParameters);
                                } catch (IllegalAccessException e) {
                                    throw new JSONDeserializerException("Unable to invoke method " + method.getName() +
                                            " in " + baseClass.getCanonicalName() + ".");
                                } catch (InvocationTargetException e) {
                                    throw new JSONDeserializerException("Internal exception in method " + method.getName() + " in " +
                                            baseClass.getCanonicalName() + ": " + e.getCause().getMessage());
                                }
                            }
                        }
                    }
                } else if (baseClass == List.class) {
                    if (!jsonEntry.isArray()) {
                        throw new JSONDeserializerException("This library does not support converting any JSON type except JSON arrays to Java Lists.");
                    }
                    JSONEntry[] arrayEntries = ((JSONArray) jsonEntry).getArray();
                    if (typeVariableMap.size() != 1) {
                        throw new JSONDeserializerException("This library does not support raw types for Java Collections.");
                    }
                    Type listType = typeVariableMap.get(LIST_TYPE_VARIABLE);
                    List<Object> newList = new ArrayList<>();
                    for (JSONEntry arrayEntry : arrayEntries) {
                        newList.add(deserialize(arrayEntry, listType));
                    }
                    deserializedObject = newList;
                } else if (Set.class.isAssignableFrom(baseClass)) {
                    deserializedObject = null;
                } else if (Map.class.isAssignableFrom(baseClass)) {
                    deserializedObject = null;
                } else if (baseClass.isArray()) {
                    if (!jsonEntry.isArray()) {
                        throw new JSONDeserializerException("This library does not support converting other JSON types besides arrays to Java arrays.");
                    }
                    Class<?> arrayComponentType = baseClass.getComponentType();
                    JSONEntry[] arrayJSONEntries = ((JSONArray) jsonEntry).getArray();
                    int arrayEntriesCount = arrayJSONEntries.length;
                    Object array = Array.newInstance(arrayComponentType, arrayEntriesCount);
                    for (int i = 0; i < arrayEntriesCount; i++) {
                        Array.set(array, i, deserialize(arrayJSONEntries[i], arrayComponentType));
                    }
                    deserializedObject = array;
                } else if (baseClass.isPrimitive()) {
                    if (baseClass == byte.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().byteValue();
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to byte.");
                        }
                    } else if (baseClass == short.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().shortValue();
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to short.");
                        }
                    } else if (baseClass == int.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().intValue();
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to int.");
                        }
                    } else if (baseClass == long.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().longValue();
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to long.");
                        }
                    } else if (baseClass == float.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().longValue();
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to float.");
                        }
                    } else if (baseClass == double.class) {
                        if (jsonEntry.isNumber()) {
                            deserializedObject = ((JSONNumber) jsonEntry).getNumber().doubleValue();
                        } else {
                            throw new JSONDeserializerException(
                                    "Cannot convert " + jsonEntry.getType() + " to double.");
                        }
                    } else if (baseClass == boolean.class) {
                        if (jsonEntry.isBoolean()) {
                            deserializedObject = ((JSONBoolean) jsonEntry).getBoolean();
                        } else {
                            throw new JSONDeserializerException(
                                    "Cannot convert " + jsonEntry.getType() + " to boolean.");
                        }
                    } else /* if (baseClass == char.class) */ {
                        if (jsonEntry.isString()) {
                            String str = ((JSONString) jsonEntry).getString();
                            if (str.length() >= 1) {
                                deserializedObject = str.charAt(0);
                            } else {
                                throw new JSONDeserializerException(
                                        "String cannot be converted to char because there are no chars in the string.");
                            }
                        } else {
                            throw new JSONDeserializerException("Cannot convert " + jsonEntry.getType() + " to char.");
                        }
                    }
                } else if (baseClass == String.class) {
                    if (jsonEntry.isString()) {
                        deserializedObject = unescapeString(((JSONString) jsonEntry).getString());
                    } else {
                        deserializedObject = jsonEntry.toString();
                    }
                } else if (Number.class.isAssignableFrom(baseClass)) {
                    if (jsonEntry.isNumber()) {
                        Number number = ((JSONNumber) jsonEntry).getNumber();
                        if (baseClass == Number.class) {
                            deserializedObject = number;
                        } else if (baseClass == Byte.class) {
                            deserializedObject = Byte.valueOf(number.byteValue());
                        } else if (baseClass == Short.class) {
                            deserializedObject = Short.valueOf(number.shortValue());
                        } else if (baseClass == Integer.class) {
                            deserializedObject = Integer.valueOf(number.intValue());
                        } else if (baseClass == Long.class) {
                            deserializedObject = Long.valueOf(number.longValue());
                        } else if (baseClass == Float.class) {
                            deserializedObject = Float.valueOf(number.floatValue());
                        } else if (baseClass == Double.class) {
                            deserializedObject = Double.valueOf(number.doubleValue());
                        } else {
                            throw new JSONDeserializerException(
                                    "Cannot deserialize this JSON into child class of Number " +
                                    baseClass.getCanonicalName());
                        }
                    } else {
                        throw new JSONDeserializerException(
                                "Cannot deserialize this JSON into a number deserializedType.");
                    }
                } else if (baseClass == Boolean.class) {
                    if (jsonEntry.isBoolean()) {
                        deserializedObject = Boolean.valueOf(((JSONBoolean) jsonEntry).getBoolean());
                    } else {
                        throw new JSONDeserializerException(
                                "Cannot convert JSON " + jsonEntry.getType() + " to Boolean.");
                    }
                } else if (baseClass == Character.class) {
                    if (jsonEntry.isString()) {
                        String str = ((JSONString) jsonEntry).getString();
                        if (str.length() >= 1) {
                            deserializedObject = Character.valueOf(str.charAt(0));
                        } else {
                            throw new JSONDeserializerException(
                                    "Cannot convert JSON String to Character because there are no characters in the string.");
                        }
                    } else {
                        throw new JSONDeserializerException(
                                "Cannot conver JSON " + jsonEntry.getType() + " to Character.");
                    }
                } else if (baseClass.isEnum()) {
                    if (jsonEntry.isString()) {
                        String rawStr = ((JSONString) jsonEntry).getString();
                        String reducedStr = rawStr.replace("_", "").replace("-", "").replace(" ", "").toLowerCase();
                        Enum<?>[] enumConstants = (Enum[]) baseClass.getEnumConstants();
                        Enum<?> foundEnum = null;
                        for (Enum<?> enumConstant : enumConstants) {
                            String enumName = enumConstant.name().replace("_", "").replace("-", "").toLowerCase();
                            String enumString = enumConstant.toString();
                            if (reducedStr.equals(enumName) || rawStr.equals(enumString)) {
                                foundEnum = enumConstant;
                                break;
                            }
                        }
                        if (foundEnum != null) {
                            deserializedObject = foundEnum;
                        } else {
                            throw new JSONDeserializerException("Cannot convert JSON String " + jsonEntry +
                                    " to enum constant of type " + baseClass.getCanonicalName() + 
                                    "since there are no enum constants that have a similar name.");
                        }
                    } else {
                        throw new JSONDeserializerException(
                                "Cannot conver JSON " + jsonEntry.getType() + " to Enum value.");
                    }
                } else {
                    deserializedObject = null;
                }
            }
        }
        return deserializedObject;
    }

    /**
     * Deserializes a {@link JSONEntry} to a Java type specified by a given
     * {@link TypeMarker}. Returns an object of the same type as the
     * {@code TypeMarker}.
     * 
     * @param <T>        the Java type that the JSON is being deserialized to; it
     *                   must agree with the TypeMarker
     * @param jsonEntry  the JSON data to convert to a Java object
     * @param typeMarker the type of Java object to create
     * @return the newly created and populated Java object of type {@code <T>}
     * @throws NullPointerException      if the JSON or type marker is {@code null}
     * @throws IllegalArgumentException  if the {@code TypeMarker} is not fully
     *                                   resolved (if it contains type variables)
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(JSONEntry jsonEntry, TypeMarker<T> typeMarker)
            throws NullPointerException, IllegalArgumentException, JSONDeserializerException {
        if (typeMarker == null) {
            throw new NullPointerException("Type marker is null.");
        }
        Type deserializedType = typeMarkerToType(typeMarker);
        if (!isResolved(deserializedType)) {
            throw new IllegalArgumentException(
                    "The Type Marker provided is not completely resolved. Please remove all type variables from the declaration of the TypeMarker.");
        }
        @SuppressWarnings("unchecked")
        T castObject = (T) deserialize(jsonEntry, deserializedType);
        return castObject;
    }

    /**
     * Deserializes a {@code JSONEntry} to a Java type specified by a given
     * {@code Class}. Returns an object of the same type as the {@code Class}.
     * 
     * @param <T>       the Java type that the JSON is being deserialized to; it
     *                  must agree with the {@code Class}
     * @param jsonEntry the JSON data to convert to a Java object
     * @param typeClass the type of Java object to create
     * @return the newly created and populated Java object of type {@code <T>}
     * @throws NullPointerException      if the JSON or type class is {@code null}
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(JSONEntry jsonEntry, Class<T> typeClass)
            throws NullPointerException, JSONDeserializerException {
        @SuppressWarnings("unchecked")
        T castObject = (T) deserialize(jsonEntry, (Type) typeClass); // cast avoids ambiguity
        return castObject;
    }

    /**
     * Parses a string using {@link JSONParser}, then deserializes the JSON to an object of
     * the type specified in the {@code TypeMarker}.
     * 
     * @param <T>        the Java type that the JSON is being deserialized to; it 
     *                   must agree with the TypeMarker
     * @param jsonString the string to parse and deserialize
     * @param typeMarker the type of Java object to create
     * @return the newly created and populated Java object of type T
     * @throws NullPointerException      if the string or type marker is null
     * @throws IllegalArgumentException  if the type marker contains an unresolved type variable
     * @throws JSONParserException       if there is a syntax error in the JSON in the string
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(String jsonString, TypeMarker<T> typeMarker) throws
            NullPointerException, IllegalArgumentException, JSONParserException, JSONDeserializerException {
        JSON json = JSONParser.parseJSON(jsonString);
        return deserialize(json, typeMarker);
    }

    /**
     * Parses a string using {@link JSONParser}, then deserializes the JSON to an object of
     * the type specified in the {@code Class}.
     * 
     * @param <T>        the Java type that the JSON is being deserialized to; it must agree with the TypeMarker
     * @param jsonString the string to parse and deserialize
     * @param typeClass  the type of Java object to create
     * @return the newly created and populated Java object of type T
     * @throws NullPointerException      if the string or type marker is null
     * @throws JSONParserException       if there is a syntax error in the JSON in the string
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(String jsonString, Class<T> typeClass) throws
            NullPointerException, JSONParserException, JSONDeserializerException {
        JSON json = JSONParser.parseJSON(jsonString);
        return deserialize(json, typeClass);
    }

    /**
     * Parses the contents of the file as a string using {@link JSONParser}, then deserializes the
     * JSON to an object of the type specified in the {@code TypeMarker}.
     * 
     * @param <T>        the Java type that the JSON is being deserialized to; it must agree with the TypeMarker
     * @param jsonFile   the file to parse and deserialize
     * @param typeMarker the type of Java object to create
     * @return the newly created and populated Java object of type T
     * @throws NullPointerException      if the file or type marker is null
     * @throws IllegalArgumentException  if the type marker contains an unresolved type variable
     * @throws IOException               if there is an error when reading the file
     * @throws JSONParserException       if there is a syntax error in the JSON in the file
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(File jsonFile, TypeMarker<T> typeMarker) throws
            NullPointerException, IllegalArgumentException, IOException, JSONParserException, JSONDeserializerException {
        JSON json = JSONParser.parseJSON(jsonFile);
        return deserialize(json, typeMarker);
    }

    /**
     * Parses the contents of the file as a string using {@link JSONParser}, then deserializes the
     * JSON to an object of the type specified in the {@code Class}.
     * 
     * @param <T>       the Java type that the JSON is being deserialized to; it must agree with the TypeMarker
     * @param jsonFile  the file to parse and deserialize
     * @param typeClass the type of Java object to create
     * @return the newly created and populated Java object of type T
     * @throws NullPointerException      if the file or type marker is null
     * @throws IOException               if there is an error when reading the file
     * @throws JSONParserException       if there is a syntax error in the JSON in the file
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    public static <T> T deserialize(File jsonFile, Class<T> typeClass) throws
            NullPointerException, IOException, JSONParserException, JSONDeserializerException {
        JSON json = JSONParser.parseJSON(jsonFile);
        return deserialize(json, typeClass);
    }

    /**
     * This private internal method is used to prepare the array of objects that
     * should be passed into a method while deserializing. It exists to simplify the
     * code and reduce repetition.
     * 
     * @param jsonEntry       the JSON data to deserialize from
     * @param executable      the method or constructor to prepare parameters for
     * @param typeVariableMap the table that connects type variables to resolved
     *                        types
     * @return the prepared parameters as an array of Objects
     * @throws JSONDeserializerException if there is an error while deserializing
     */
    private static Object[] prepareParameters(JSONEntry jsonEntry, Executable executable,
            Map<TypeVariable<?>, Type> typeVariableMap) throws JSONDeserializerException {
        Class<?> executableClass = executable.getDeclaringClass();
        Parameter[] parameters = executable.getParameters();
        int parameterCount = executable.getParameterCount();
        Object[] preparedParameters = new Object[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            JSONEntry entry;
            if (parameters[i].isAnnotationPresent(DeserializedJSONObjectValue.class)) {
                if (!jsonEntry.isObject()) {
                    throw new JSONDeserializerException("Cannot use \"DeserializedJSONObjectValue\" since the given JSON entry is not a JSON object.");
                }
                JSONObject jsonObject = (JSONObject) jsonEntry;
                String key = parameters[i].getAnnotation(DeserializedJSONObjectValue.class).key();
                if (!(jsonObject.containsKey(key))) {
                    throw new JSONDeserializerException(
                            "Parameter index " + i + " of executable " + executable.toString() + " in Class "
                                    + executableClass.getCanonicalName() + " requests the key \"" + key
                                    + "\" in a JSON Object, but the JSON Object does not contain that key.");
                }
                entry = jsonObject.get(key);
            } else if (parameters[i].isAnnotationPresent(DeserializedJSONArrayItem.class)) {
                if (!jsonEntry.isArray()) {
                    throw new JSONDeserializerException("Cannot use \"DeserializedJSONArrayItem\" since the given JSON entry is not a JSON array.");
                }
                JSONArray jsonArray = (JSONArray) jsonEntry;
                int index = parameters[i].getAnnotation(DeserializedJSONArrayItem.class).index();
                if (index < 0 || index >= jsonArray.getArray().length) {
                    throw new JSONDeserializerException(
                            "Parameter index " + i + " of executable " + executable.toString() + " in Class "
                                    + executableClass.getCanonicalName() + " requests the array value at index " + index
                                    + " in a JSON Array, but the index is out of bounds for the array.");
                }
                entry = jsonArray.get(index);
            } else if (parameters[i].isAnnotationPresent(DeserializedJSONEntry.class)) {
                entry = jsonEntry;
            } else {
                throw new JSONDeserializerException("All parameters of a method annotated with \"DeserializedJSONTarget\" must be annotated with \"DeserializedJSONObjectValue\", \"DeserializedJSONArrayItem\", or \"DeserializedJSONEntry\".");
            }
            Type resolvedParameterType = resolveType(parameters[i].getParameterizedType(), typeVariableMap);
            preparedParameters[i] = deserialize(entry, resolvedParameterType);
        }
        return preparedParameters;
    }

    /**
     * Extracts the {@link Type} from a {@link TypeMarker}. For example,
     * <pre>typeMarkerToType(new TypeMarker&lt;List&lt;Integer&gt;&gt;() {})</pre> returns a
     * {@link ParameterizedType} corresponding to {@code List<Integer>}.
     * 
     * @param typeMarker the {@code TypeMarker} to be converted to a {@code Type}
     * @return the converted {@code Type}
     * @throws IllegalArgumentException if the {@code TypeMarker} is not an annonymous inner
     *                                  class or direct implementation of
     *                                  {@code TypeMarker}
     */
    private static Type typeMarkerToType(TypeMarker<?> typeMarker) throws IllegalArgumentException {
        Type deserializedType;
        Class<?> typeMarkerClass = typeMarker.getClass();
        Type[] typeInterfaces = typeMarkerClass.getGenericInterfaces();
        if (typeInterfaces.length != 1 || !(typeInterfaces[0] instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Type marker object should be an instance of a class that directly implements exactly one parameterized interface.");
        }
        ParameterizedType typeMarkerInterface = (ParameterizedType) typeInterfaces[0];
        if (((Class<?>) typeMarkerInterface.getRawType()) != TypeMarker.class) {
            throw new IllegalArgumentException(
                    "Type marker object should be an instance of a class that directly implements exactly one parameterized interface of type TypeMarker.");
        }
        deserializedType = typeMarkerInterface.getActualTypeArguments()[0];
        return deserializedType;
    }

    /**
     * This method removes the escape sequences from JSON strings and returns strings that 
     * contain the actual characters. This can be used both internally in deserialization and publicly by users.
     * 
     * @param escapedString the escaped string to be unescaped
     * @return the unescaped string
     * @throws NullPointerException if the escaped string is null
     * @throws JSONDeserializerException if the string contains an invalid escape character
     */
    public static String unescapeString(String escapedString) throws NullPointerException, JSONDeserializerException {
        if (escapedString == null) {
            throw new NullPointerException("The escaped string is null.");
        }
        StringBuilder unescapedString = new StringBuilder();
        int escapedStringLength = escapedString.length();
        int i = 0;
        int lastEscapedCharNextIndex = 0;
        while (i < escapedStringLength) {
            char charAtI = escapedString.charAt(i);
            i++;
            if (charAtI == '\\') {
                unescapedString.append(escapedString.substring(lastEscapedCharNextIndex, i - 1));
                char escapeChar = escapedString.charAt(i);
                i++;
                switch (escapeChar) {
                    case '"':
                        unescapedString.append("\"");
                    break;
                    case '\\':
                        unescapedString.append("\\");
                    break;
                    case '/':
                        unescapedString.append("/");
                    break;
                    case 'b':
                        unescapedString.append("\b");
                    break;
                    case 'f':
                        unescapedString.append("\f");
                    break;
                    case 'n':
                        unescapedString.append("\n");
                    break;
                    case 'r':
                        unescapedString.append("\\");
                    break;
                    case 't':
                        unescapedString.append("\t");
                    break;
                    case 'u':
                        String codeString = escapedString.substring(i, i + 4);
                        i += 4;
                        char decodedChar;
                        try {
                            short parsedCode = Short.parseShort(codeString, 16);
                            decodedChar = (char) parsedCode;
                        } catch (NumberFormatException e) {
                            throw new JSONDeserializerException("Invalid escaped Unicode char point. If you use \\u, make sure that the four characters after are hexadecimal.");
                        }
                        unescapedString.append(decodedChar);
                    break;
                    default:
                        throw new JSONDeserializerException("Unable to unescape string: Found invalid sequence \"" + escapeChar + ", make sure that all escape characters are valid.");
                }
                lastEscapedCharNextIndex = i;
            }
        }
        unescapedString.append(escapedString.substring(lastEscapedCharNextIndex, i));
        return unescapedString.toString();
    }
}