package com.jlbabilino.json;

import static com.jlbabilino.json.ResolvedTypes.resolveClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

final class JSONClassModel {

    static final class SerializedClassModel {

        private final Class<?> cls;

        private List<Field>                                          serializedJSONEntryFields = new ArrayList<>();
        private List<AnnotatedJSONMethod<SerializedJSONEntry>>       serializedJSONEntryMethods = new ArrayList<>();
        private List<Field>                                          serializedJSONObjectValueFields = new ArrayList<>();
        private List<AnnotatedJSONMethod<SerializedJSONObjectValue>> serializedJSONObjectValueMethods = new ArrayList<>();
        private List<Field>                                          serializedJSONArrayItemFields = new ArrayList<>();
        private List<AnnotatedJSONMethod<SerializedJSONArrayItem>>   serializedJSONArrayItemMethods = new ArrayList<>();

        List<Field>                                          serializedJSONEntryFieldsUnmodifiable =        Collections.unmodifiableList(serializedJSONEntryFields);
        List<AnnotatedJSONMethod<SerializedJSONEntry>>       serializedJSONEntryMethodsUnmodifiable =       Collections.unmodifiableList(serializedJSONEntryMethods);
        List<Field>                                          serializedJSONObjectValueFieldsUnmodifiable =  Collections.unmodifiableList(serializedJSONObjectValueFields);
        List<AnnotatedJSONMethod<SerializedJSONObjectValue>> serializedJSONObjectValueMethodsUnmodifiable = Collections.unmodifiableList(serializedJSONObjectValueMethods);
        List<Field>                                          serializedJSONArrayItemFieldsUnmodifiable =    Collections.unmodifiableList(serializedJSONArrayItemFields);
        List<AnnotatedJSONMethod<SerializedJSONArrayItem>>   serializedJSONArrayItemMethodsUnmodifiable =   Collections.unmodifiableList(serializedJSONArrayItemMethods);

        SerializedClassModel(Class<?> cls) throws InvalidJSONTranslationConfiguration {
            this.cls = cls;
            collectSerializableClassModel(this.cls);
            if (serializedJSONEntryFields.size() + serializedJSONEntryMethods.size() > 1) {
                throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                        + System.lineSeparator() + System.lineSeparator()
                        + this.cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "defines multiple methods or fields that serialize the entire JSON entry, which is ambiguous. Please only define one method or field with the @SerializedJSONEntry annotation.");
            }
            checkSerializedObjectConditions();
            checkSerializedArrayConditions();
        }

        private void collectSerializableClassModel(Class<?> cls) throws InvalidJSONTranslationConfiguration {
            if (cls != null && cls != Object.class) {
                for (Class<?> superInterface : cls.getInterfaces()) {
                    collectSerializableClassModel(superInterface);
                }
                collectSerializableClassModel(cls.getSuperclass());
                Field[] clsDeclaredFields = cls.getDeclaredFields();
                Method[] clsDeclaredMethods = cls.getDeclaredMethods();
                for (Field field : clsDeclaredFields) {
                    if (field.isAnnotationPresent(SerializedJSONEntry.class)) {
                        serializedJSONEntryFields.add(field);
                    } else if (field.isAnnotationPresent(SerializedJSONArrayItem.class)) {
                        serializedJSONArrayItemFields.add(field);
                    } else if (field.isAnnotationPresent(SerializedJSONObjectValue.class)) {
                        serializedJSONObjectValueFields.add(field);
                    }
                }
                for (Method clsMethod : clsDeclaredMethods) {
                    if (addMethodToSerializedModel(clsMethod, serializedJSONEntryMethods, SerializedJSONEntry.class)) continue;
                    if (addMethodToSerializedModel(clsMethod, serializedJSONObjectValueMethods, SerializedJSONObjectValue.class)) continue;
                    if (addMethodToSerializedModel(clsMethod, serializedJSONArrayItemMethods, SerializedJSONArrayItem.class)) continue;
                }
            }
        }

        private <A extends Annotation> boolean addMethodToSerializedModel(Method clsMethod, List<AnnotatedJSONMethod<A>> superMethods, Class<A> targetAnnotationClass) throws InvalidJSONTranslationConfiguration {
            return addMethodToModel(clsMethod, superMethods, targetAnnotationClass, SerializedClassModel::checkSerializedJSONMethod);
        }

        private void checkSerializedObjectConditions() throws InvalidJSONTranslationConfiguration {
            Set<String> keySet = new HashSet<>();
            for (Field field : serializedJSONObjectValueFields) {
                String key = field.getAnnotation(SerializedJSONObjectValue.class).key();
                if (keySet.contains(key)) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines multiple methods or fields that serialize the same key \""
                            + JSONSerializer.escapeString(key)
                            + "\", which is ambiguous. Please only define one method or field with the @SerializedJSONObject(key = \""
                            + JSONSerializer.escapeString(key) + "\") annotation.");
                } else {
                    keySet.add(key);
                }
            }
            for (AnnotatedJSONMethod<SerializedJSONObjectValue> method : serializedJSONObjectValueMethods) {
                String key = method.getAnnotation().key();
                if (keySet.contains(key)) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines multiple methods or fields that serialize the same object key \""
                            + JSONSerializer.escapeString(key)
                            + "\", which is ambiguous. Please only define one method or field with the @SerializedJSONObjectValue(key = \""
                            + JSONSerializer.escapeString(key) + "\") annotation.");
                } else {
                    keySet.add(key);
                }
            }
        }

        private void checkSerializedArrayConditions() throws InvalidJSONTranslationConfiguration {
            SortedSet<Integer> arrayIndices = new TreeSet<>();
            for (Field field : serializedJSONArrayItemFields) {
                int index = field.getAnnotation(SerializedJSONArrayItem.class).index();
                if (arrayIndices.contains(index)) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines multiple methods or fields that serialize the same array index " + index
                            + ", which is ambiguous. Please only define one method or field with the @SerializedJSONArrayItem(index = "
                            + index + ") annotation.");
                } else if (index < 0) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines a method or field that serializes to the negative array index " + index
                            + ". Serializing to negative array indices is not allowed.");
                } else {
                    arrayIndices.add(index);
                }
            }
            for (AnnotatedJSONMethod<SerializedJSONArrayItem> method : serializedJSONArrayItemMethods) {
                int index = method.getAnnotation().index();
                if (arrayIndices.contains(index)) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines multiple methods or fields that serialize the same array index " + index
                            + ", which is ambiguous. Please only define one method or field with the @SerializedJSONArrayItem(index = "
                            + index + ") annotation.");
                } else if (index < 0) {
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines a method or field that serializes to the negative array index " + index
                            + ". Serializing to negative array indices is not allowed.");
                } else {
                    arrayIndices.add(index);
                }
            }
            if (arrayIndices.size() > 0) {
                int lastValue = arrayIndices.last();
                int correctSize = lastValue + 1;
                if (arrayIndices.size() < correctSize) {
                    List<Integer> arrayIndicesList = new ArrayList<>(arrayIndices);
                    List<Integer> missingIndicesList = new ArrayList<>();
                    int actualIndex = 0;
                    for (int correctIndex = 0; correctIndex < correctSize; correctIndex++) {
                        if (arrayIndicesList.get(actualIndex) != correctIndex) {
                            missingIndicesList.add(correctIndex);
                        } else {
                            actualIndex++;
                        }
                    }
                    String definedIndicesStr = arrayIndicesList.toString();
                    definedIndicesStr = definedIndicesStr.substring(1, definedIndicesStr.length() - 1);
                    String undefinedIndicesStr = missingIndicesList.toString();
                    undefinedIndicesStr = undefinedIndicesStr.substring(1, undefinedIndicesStr.length() - 1);
                    throw new InvalidJSONTranslationConfiguration("Serialized JSON class"
                            + System.lineSeparator() + System.lineSeparator()
                            + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "defines fields or methods that serialize to JSON array indices:"
                            + System.lineSeparator() + System.lineSeparator()
                            + definedIndicesStr + System.lineSeparator() + System.lineSeparator()
                            + ", but it fails to define fields or methods for some indices in the range [0, "
                            + lastValue + "]:" + System.lineSeparator() + System.lineSeparator()
                            + undefinedIndicesStr);
                }
            }
        }

        private static void checkSerializedJSONMethod(Executable method) throws InvalidJSONTranslationConfiguration {
            checkZeroTypeParameters(method);
            if (method.getParameterCount() > 0) {
                throw new InvalidJSONTranslationConfiguration("Serialized JSON method"
                        + System.lineSeparator() + System.lineSeparator()
                        + method.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must not have any formal parameters.");
            }
        }

    }

    static final class DeserializedClassModel {

        private Method determiner;
        private Executable constructor;

        private List<Field>                                       deserializedJSONEntryFields = new ArrayList<>();
        private List<Field>                                       deserializedJSONObjectValueFields = new ArrayList<>();
        private List<Field>                                       deserializedJSONArrayItemFields = new ArrayList<>();
        private List<AnnotatedJSONMethod<DeserializedJSONTarget>> deserializedJSONTargetMethods = new ArrayList<>();

        List<Field>                                       deserializedJSONEntryFieldsUnmodifiable =       Collections.unmodifiableList(deserializedJSONEntryFields);
        List<Field>                                       deserializedJSONObjectValueFieldsUnmodifiable = Collections.unmodifiableList(deserializedJSONObjectValueFields);
        List<Field>                                       deserializedJSONArrayItemFieldsUnmodifiable =   Collections.unmodifiableList(deserializedJSONArrayItemFields);
        List<AnnotatedJSONMethod<DeserializedJSONTarget>> deserializedJSONTargetMethodsUnmodifiable =     Collections.unmodifiableList(deserializedJSONTargetMethods);

        DeserializedClassModel(Class<?> cls) throws InvalidJSONTranslationConfiguration {
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(DeserializedJSONDeterminer.class)) {
                    checkDeterminerConditions(method);
                    determiner = method;
                    break;
                }
                if (method.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                    checkFactoryMethodConditions(method);
                    constructor = method;
                    break;
                }
            }
            if (!Modifier.isAbstract(cls.getModifiers()) && !cls.isInterface()) {
                Constructor<?>[] constructors = cls.getDeclaredConstructors();
                for (Constructor<?> declaredConstructor : constructors) {
                    if (declaredConstructor.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                        checkZeroTypeParameters(declaredConstructor);
                        checkDeserializedExecutableConditions(declaredConstructor);
                        constructor = declaredConstructor;
                        break;
                    }
                }
            }
            if (constructor == null) {
                throw new InvalidJSONTranslationConfiguration(
                        "Unable to locate a suitable constructor or factory method to instantiate type"
                        + System.lineSeparator() + System.lineSeparator() + cls.getCanonicalName());
            }
            collectDeserializableClassModel(cls);
        }

        Method getDeterminer() {
            return determiner;
        }
    
        Executable getConstructor() {
            return constructor;
        }

        private void collectDeserializableClassModel(Class<?> cls) throws InvalidJSONTranslationConfiguration {
            if (cls != null && cls != Object.class) {
                for (Class<?> superInterface : cls.getInterfaces()) {
                    collectDeserializableClassModel(superInterface);
                }
                collectDeserializableClassModel(cls.getSuperclass());
                Field[] clsDeclaredFields = cls.getDeclaredFields();
                Method[] clsDeclaredMethods = cls.getDeclaredMethods();
                for (Field field : clsDeclaredFields) {
                    if (field.isAnnotationPresent(DeserializedJSONEntry.class)) {
                        deserializedJSONEntryFields.add(field);
                    } else if (field.isAnnotationPresent(DeserializedJSONObjectValue.class)) {
                        deserializedJSONObjectValueFields.add(field);
                    } else if (field.isAnnotationPresent(DeserializedJSONArrayItem.class)) {
                        deserializedJSONArrayItemFields.add(field);
                    }
                }
                for (Method clsMethod : clsDeclaredMethods) {
                    if (addMethodToDeserializedModel(clsMethod, deserializedJSONTargetMethods, DeserializedJSONTarget.class)) continue;
                }
            }
        }

        private <A extends Annotation> boolean addMethodToDeserializedModel(Method clsMethod, List<AnnotatedJSONMethod<A>> superMethods, Class<A> targetAnnotationClass) throws InvalidJSONTranslationConfiguration {
            return addMethodToModel(clsMethod, superMethods, targetAnnotationClass, DeserializedClassModel::checkDeserializedExecutableConditions);
        }

        private static void checkDeserializedExecutableConditions(Executable executable) throws InvalidJSONTranslationConfiguration {
            checkZeroTypeParameters(executable);
            for (Parameter parameter : executable.getParameters()) {
                if (!parameter.isAnnotationPresent(DeserializedJSONEntry.class)
                        && !parameter.isAnnotationPresent(DeserializedJSONObjectValue.class)
                        && !parameter.isAnnotationPresent(DeserializedJSONArrayItem.class)) {
                    throw new InvalidJSONTranslationConfiguration("JSON Deserialized executable"
                            + System.lineSeparator() + System.lineSeparator()
                            + executable.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "contains a parameter \"" + parameter.toString()
                            + "\", which does not use any JSON deserializer annotations. Please annotate it with at least one of the following annotations: @DeserializedJSONEntry, @DeserializedJSONObjectValue, or @DeserializedJSONArrayItem.");
                }
            }
        }

        private static void checkDeterminerConditions(Method determiner) throws InvalidJSONTranslationConfiguration {
            checkDeserializedExecutableConditions(determiner);
            Class<?> cls = determiner.getDeclaringClass();
            if (Modifier.isFinal(cls.getModifiers())) {
                throw new InvalidJSONTranslationConfiguration("A determiner cannot exist in final class"
                        + System.lineSeparator() + System.lineSeparator()
                        + cls.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "since it would be redundant. The only result it could return would be this type.");
            }
            if (!Modifier.isStatic(determiner.getModifiers())) {
                throw new InvalidJSONTranslationConfiguration("Determiner " + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + " must be a static method.");
            }
            if (determiner.getExceptionTypes().length > 1
                    || (determiner.getExceptionTypes().length == 1
                    && determiner.getExceptionTypes()[0] != JSONDeserializerException.class)) {
                throw new InvalidJSONTranslationConfiguration("Determiner " + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + " can throw up to one exception, and it can only throw exceptions of type \"JSONDeserializerException\"");
            }
            if (determiner.getTypeParameters().length > 0) {
                throw new InvalidJSONTranslationConfiguration("Determiner" + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "should not be a generic method.");
            }
            if (!(determiner.getReturnType() == TypeMarker.class || determiner.getReturnType() == Class.class) ||
                !(determiner.getGenericReturnType() instanceof ParameterizedType)) {
                ResolvedParameterizedType recommendedType = new ResolvedParameterizedType(cls.getDeclaringClass(), Class.class, new Type[]{new ResolvedWildcardType(new Type[0], new Type[0])});
                throw new InvalidJSONTranslationConfiguration("Determiner" + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must return a parameterized Class<> or TypeMarker<>. For example, it could return"
                        + System.lineSeparator() + System.lineSeparator()
                        + recommendedType.toString() + System.lineSeparator() + System.lineSeparator()
                        + "or a parameterized version if this is a generic type.");
            }
            ParameterizedType genericReturnType = (ParameterizedType) determiner
                    .getGenericReturnType();
            if (genericReturnType.getActualTypeArguments()[0] instanceof Class<?>) {
                if (!cls.isAssignableFrom((Class<?>) genericReturnType.getActualTypeArguments()[0])) {
                    throw new InvalidJSONTranslationConfiguration("Determiner" + System.lineSeparator() + System.lineSeparator()
                            + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "must return Class<TYPE> or TypeMarker<TYPE> where TYPE is this type or a subtype.");
                }
            } else if (genericReturnType.getActualTypeArguments()[0] instanceof WildcardType) {
                WildcardType typeMarkerWildcard = (WildcardType) genericReturnType
                        .getActualTypeArguments()[0];
                Type wildcardUpperBound = typeMarkerWildcard.getUpperBounds()[0];
                if (!cls.isAssignableFrom(resolveClass(wildcardUpperBound))) {
                    throw new InvalidJSONTranslationConfiguration("Determiner" + System.lineSeparator() + System.lineSeparator()
                            + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "must return Class<? extends TYPE> or TypeMarker<? extends TYPE> where TYPE is this type or a subtype.");
                }
            } else {
                throw new InvalidJSONTranslationConfiguration("Determiner" + System.lineSeparator() + System.lineSeparator() 
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must return TypeMarker<? extends TYPE>, Class<? extends TYPE>, TypeMarker<TYPE>, or Class<TYPE> where TYPE is this type or a subtype.");
            }
        }

        private static void checkFactoryMethodConditions(Method factoryMethod) throws InvalidJSONTranslationConfiguration {
            checkDeserializedExecutableConditions(factoryMethod);
            Class<?> cls = factoryMethod.getDeclaringClass();
            Type[] clsTypeParameters = cls.getTypeParameters();
            int factoryMethodModifiers = factoryMethod.getModifiers();
            if (!Modifier.isStatic(factoryMethodModifiers)) {
                throw new InvalidJSONTranslationConfiguration("Factory method"
                        + System.lineSeparator() + System.lineSeparator()
                        + factoryMethod.toGenericString()
                        + System.lineSeparator() + System.lineSeparator() + "must be static.");
            }
            if (factoryMethod.getTypeParameters().length != clsTypeParameters.length) {
                throw new InvalidJSONTranslationConfiguration("Factory method" + System.lineSeparator() + System.lineSeparator()
                        + factoryMethod.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must be a generic method with the same number of type parameters as its declaring class:"
                        + System.lineSeparator() + System.lineSeparator() + cls.toGenericString());
            }
            if (factoryMethod.getReturnType() != cls) {
                throw new InvalidJSONTranslationConfiguration("Factory method"
                        + System.lineSeparator() + System.lineSeparator()
                        + factoryMethod.toGenericString()
                        + "must return" + System.lineSeparator() + System.lineSeparator()
                        + cls.getCanonicalName());
            }
            if (clsTypeParameters.length > 0) {
                if (!(factoryMethod.getGenericReturnType() instanceof ParameterizedType)) {
                    throw new InvalidJSONTranslationConfiguration("Factory method"
                            + System.lineSeparator() + System.lineSeparator() + factoryMethod.toGenericString()
                            + System.lineSeparator() + System.lineSeparator()
                            + "must not return a raw type since raw types are not supported.");
                }
                ParameterizedType factoryParameterizedReturnType = (ParameterizedType) factoryMethod.getGenericReturnType();
                if (!Arrays.equals(factoryParameterizedReturnType.getActualTypeArguments(), factoryMethod.getTypeParameters())) {
                    throw new InvalidJSONTranslationConfiguration("Factory method" + System.lineSeparator() + System.lineSeparator()
                            + factoryMethod.toGenericString() + System.lineSeparator() + System.lineSeparator()
                            + "must return" + System.lineSeparator() + System.lineSeparator()
                            + new ResolvedParameterizedType(null, cls, factoryMethod.getTypeParameters()).toString());
                }
            }
        }
    }

    private static <A extends Annotation> boolean addMethodToModel(Method clsMethod, List<AnnotatedJSONMethod<A>> superMethods, Class<A> targetAnnotationClass, JSONExecutableChecker executableChecker) throws InvalidJSONTranslationConfiguration {
        for (int i = superMethods.size() - 1; i >= 0; i--) {
            if (isOverride(superMethods.get(i).method, clsMethod)) {
                A annotation;
                if (clsMethod.isAnnotationPresent(targetAnnotationClass)) { // if overriding method redefines annotation, use it
                    annotation = clsMethod.getAnnotation(targetAnnotationClass);
                } else { // otherwise just use the annotation from the super method
                    annotation = superMethods.get(i).annotation;
                }
                superMethods.remove(i);
                superMethods.add(
                        new AnnotatedJSONMethod<A>(annotation, clsMethod));
                return true;
            }
        }
        // must not be an overriding method
        if (clsMethod.isAnnotationPresent(targetAnnotationClass)) {
            executableChecker.checkMethod(clsMethod);
            AnnotatedJSONMethod<A> newMethod = new AnnotatedJSONMethod<A>(clsMethod.getAnnotation(targetAnnotationClass), clsMethod);
            superMethods.add(newMethod);
            return true;
        }
        // do nothing if not a JSON related method
        return false;
    }

    static class AnnotatedJSONMethod<A extends Annotation> {

        private A annotation;
        private Method method;

        private AnnotatedJSONMethod(A annotation, Method method) {
            this.annotation = annotation;
            this.method = method;
        }

        A getAnnotation() {
            return annotation;
        }

        Method getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return annotation.toString() + " " + method.toGenericString();
        }
    }

    private static boolean isOverride(Method superMethod, Method subMethod) {
        return !Modifier.isPrivate(superMethod.getModifiers()) && superMethod.getName().equals(subMethod.getName())
                && Arrays.equals(superMethod.getParameterTypes(), subMethod.getParameterTypes());
    }

    @FunctionalInterface
    private static interface JSONExecutableChecker {

        void checkMethod(Executable executable) throws InvalidJSONTranslationConfiguration;
    }

    private static void checkZeroTypeParameters(Executable executable) throws InvalidJSONTranslationConfiguration {
        if (executable.getTypeParameters().length != 0) {
            throw new InvalidJSONTranslationConfiguration("Method"
                    + System.lineSeparator() + System.lineSeparator()
                    + executable.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + "is invalid since generic executables beyond factory methods are not supported for JSON translation.");
        }
    }
}
