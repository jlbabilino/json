package com.jlbabilino.json;

import static com.jlbabilino.json.ResolvedTypes.resolveClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class JSONClassModel {

    private Method determiner;
    private Executable constructor;

    private List<Field>                                          serializedJSONEntryFields = new ArrayList<>();
    private List<AnnotatedJSONMethod<SerializedJSONEntry>>       serializedJSONEntryMethods = new ArrayList<>();
    private List<Field>                                          serializedJSONObjectValueFields = new ArrayList<>();
    private List<AnnotatedJSONMethod<SerializedJSONObjectValue>> serializedJSONObjectValueMethods = new ArrayList<>();
    private List<Field>                                          serializedJSONArrayItemFields = new ArrayList<>();
    private List<AnnotatedJSONMethod<SerializedJSONArrayItem>>   serializedJSONArrayItemMethods = new ArrayList<>();
    private List<Field>                                          deserializedJSONEntryFields = new ArrayList<>();
    private List<Field>                                          deserializedJSONObjectValueFields = new ArrayList<>();
    private List<Field>                                          deserializedJSONArrayItemFields = new ArrayList<>();
    private List<AnnotatedJSONMethod<DeserializedJSONTarget>>    deserializedJSONTargetMethods = new ArrayList<>();

    List<Field>                                          serializedJSONEntryFieldsUnmodifiable =         Collections.unmodifiableList(serializedJSONEntryFields);
    List<AnnotatedJSONMethod<SerializedJSONEntry>>       serializedJSONEntryMethodsUnmodifiable =        Collections.unmodifiableList(serializedJSONEntryMethods);
    List<Field>                                          serializedJSONObjectValueFieldsUnmodifiable =   Collections.unmodifiableList(serializedJSONObjectValueFields);
    List<AnnotatedJSONMethod<SerializedJSONObjectValue>> serializedJSONObjectValueMethodsUnmodifiable =  Collections.unmodifiableList(serializedJSONObjectValueMethods);
    List<Field>                                          serializedJSONArrayItemFieldsUnmodifiable =     Collections.unmodifiableList(serializedJSONArrayItemFields);
    List<AnnotatedJSONMethod<SerializedJSONArrayItem>>   serializedJSONArrayItemMethodsUnmodifiable =    Collections.unmodifiableList(serializedJSONArrayItemMethods);
    List<Field>                                          deserializedJSONEntryFieldsUnmodifiable =       Collections.unmodifiableList(deserializedJSONEntryFields);
    List<Field>                                          deserializedJSONObjectValueFieldsUnmodifiable = Collections.unmodifiableList(deserializedJSONObjectValueFields);
    List<Field>                                          deserializedJSONArrayItemFieldsUnmodifiable =   Collections.unmodifiableList(deserializedJSONArrayItemFields);
    List<AnnotatedJSONMethod<DeserializedJSONTarget>>    deserializedJSONTargetMethodsUnmodifiable =     Collections.unmodifiableList(deserializedJSONTargetMethods);
    
    private JSONClassModel() {
    }

    Method getDeterminer() {
        return determiner;
    }

    Executable getConstructor() {
        return constructor;
    }

    static JSONClassModel of(Class<?> cls) throws JSONDeserializerException {
        JSONClassModel classModel = new JSONClassModel();

        Method[] methods = cls.getDeclaredMethods();
        if (!Modifier.isFinal(cls.getModifiers())) {
            for (Method method : methods) {
                if (method.isAnnotationPresent(DeserializedJSONDeterminer.class)) {
                    checkDeterminerConditions(method);
                    classModel.determiner = method;
                    break;
                }
                if (method.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                    classModel.constructor = method;
                }
            }
        }
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : constructors) {
            if (declaredConstructor.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                classModel.constructor = declaredConstructor;
                break;
            }
        }
        collectClassModel(cls, classModel);
        
        return classModel;
    }

    private static void collectClassModel(Class<?> cls, JSONClassModel classModel) {
        for (Class<?> superInterface : cls.getInterfaces()) {
            collectClassModel(superInterface, classModel);
        }
        collectClassModel(cls.getSuperclass(), classModel);
        if (cls != null && cls != Object.class) {
            Field[] clsDeclaredFields = cls.getDeclaredFields();
            Method[] clsDeclaredMethods = cls.getDeclaredMethods();
            for (Field field : clsDeclaredFields) {
                if (field.isAnnotationPresent(SerializedJSONEntry.class)) {
                    classModel.serializedJSONEntryFields.add(field);
                } else if (field.isAnnotationPresent(SerializedJSONArrayItem.class)) {
                    classModel.serializedJSONArrayItemFields.add(field);
                } else if (field.isAnnotationPresent(SerializedJSONObjectValue.class)) {
                    classModel.serializedJSONObjectValueFields.add(field);
                }

                if (field.isAnnotationPresent(DeserializedJSONEntry.class)) {
                    classModel.deserializedJSONEntryFields.add(field);
                } else if (field.isAnnotationPresent(DeserializedJSONObjectValue.class)) {
                    classModel.deserializedJSONObjectValueFields.add(field);
                } else if (field.isAnnotationPresent(DeserializedJSONArrayItem.class)) {
                    classModel.deserializedJSONArrayItemFields.add(field);
                }
            }
            for (Method clsMethod : clsDeclaredMethods) {
                if (addMethodToModel(clsMethod, classModel.serializedJSONEntryMethods, SerializedJSONEntry.class)) break;
                if (addMethodToModel(clsMethod, classModel.serializedJSONObjectValueMethods, SerializedJSONObjectValue.class)) break;
                if (addMethodToModel(clsMethod, classModel.serializedJSONArrayItemMethods, SerializedJSONArrayItem.class)) break;
                if (addMethodToModel(clsMethod, classModel.deserializedJSONTargetMethods, DeserializedJSONTarget.class)) break;
            }
            if (!Modifier.isAbstract(cls.getModifiers()) && !cls.isInterface()) {
                Constructor<?>[] constructors = cls.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    if (constructor.isAnnotationPresent(DeserializedJSONConstructor.class)) {

                    }
                }
            }
        }
    }

    private static <A extends Annotation> boolean addMethodToModel(Method clsMethod, List<AnnotatedJSONMethod<A>> superMethods, Class<A> targetAnnotationClass) {
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
            superMethods.add(
                    new AnnotatedJSONMethod<A>(clsMethod.getAnnotation(targetAnnotationClass), clsMethod));
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
    }

    private static boolean isOverride(Method superMethod, Method subMethod) {
        return !Modifier.isPrivate(superMethod.getModifiers()) && superMethod.getName().equals(subMethod.getName())
                && Arrays.equals(superMethod.getParameterTypes(), subMethod.getParameterTypes());
    }

    private static void checkDeterminerConditions(Method determiner) throws JSONDeserializerException {
        Class<?> cls = determiner.getDeclaringClass();
        int determinerModifiers = determiner.getModifiers();
        if (!Modifier.isStatic(determinerModifiers)) {
            throw new JSONDeserializerException("Determiner \"" + determiner.toGenericString()
                    + "\" must be a static method.");
        }
        if (determiner.getExceptionTypes().length > 1
                || (determiner.getExceptionTypes().length == 1
                && determiner.getExceptionTypes()[0] != JSONDeserializerException.class)) {
            throw new JSONDeserializerException("Determiner \"" + determiner.toGenericString()
                    + "\" can throw up to one exception, and it can only throw exceptions of type \"JSONDeserializerException\"");
        }
        if (!(determiner.getReturnType() == TypeMarker.class || determiner.getReturnType() == Class.class) ||
            !(determiner.getGenericReturnType() instanceof ParameterizedType)) {
            throw new JSONDeserializerException(
                    "Determiner " + determiner.toGenericString() + " in " + cls.getCanonicalName()
                            + " must return a parameterized Class<> or TypeMarker<>");
        }
        if (determiner.getTypeParameters().length != 0) {
            throw new JSONDeserializerException("Determiner " + determiner.toGenericString() + " in "
                    + cls.getCanonicalName() + " should not be a generic method.");
        }
        ParameterizedType genericReturnType = (ParameterizedType) determiner
                .getGenericReturnType();
        if (genericReturnType.getActualTypeArguments()[0] instanceof Class<?>) {
            cls.isAssignableFrom((Class<?>) genericReturnType.getActualTypeArguments()[0]);
        } else if (genericReturnType.getActualTypeArguments()[0] instanceof WildcardType) {
            WildcardType typeMarkerWildcard = (WildcardType) genericReturnType
                    .getActualTypeArguments()[0];
            Type wildcardUpperBound = typeMarkerWildcard.getUpperBounds()[0];
            if (!cls.isAssignableFrom(resolveClass(wildcardUpperBound))) {
                throw new JSONDeserializerException("Determiner " + determiner.toGenericString() + " in "
                        + cls.getCanonicalName()
                        + " must return an upper bounded wildcard Class<? extends TYPE> or TypeMarker<? extends TYPE>, where TYPE is this interface or class or its subtypes. TYPE may also be parameterized.");
            }
        } else {
            throw new JSONDeserializerException("Determiner " + determiner.toGenericString() + " in "
                    + cls.getCanonicalName()
                    + " must return TypeMarker<? extends TYPE>, Class<? extends TYPE>, TypeMarker<TYPE>, or Class< TYPE> where TYPE is this interface or class or its subtypes.");
        }
    }

    private static void checkFactoryMethodConditions(Method factoryMethod) throws JSONDeserializerException {
        Class<?> cls = factoryMethod.getDeclaringClass();
        Type[] clsTypeParameters = cls.getTypeParameters();
        int factoryMethodModifiers = factoryMethod.getModifiers();
        if (!Modifier.isStatic(factoryMethodModifiers)) {
            throw new JSONDeserializerException("Factory method " + factoryMethod.toGenericString() + " in "
                    + cls.getCanonicalName() + " must be static.");
        }
        if (factoryMethod.getTypeParameters().length != clsTypeParameters.length) {
            throw new JSONDeserializerException("Factory method " + factoryMethod.toGenericString()
                    + " must be a generic method with the same number of type parameters as its declaring class "
                    + cls.getCanonicalName());
        }
        if (factoryMethod.getReturnType() != cls) {
            throw new JSONDeserializerException(
                    "Factory method " + factoryMethod.toGenericString() + " in " + cls.getCanonicalName()
                    + " must return " + cls.getCanonicalName());
        }
        if (clsTypeParameters.length > 0) {
            if (!(factoryMethod.getGenericReturnType() instanceof ParameterizedType)) {
                throw new JSONDeserializerException("Factory method " + factoryMethod.toGenericString() + " in "
                        + cls.getCanonicalName()
                        + " must not return a raw type since raw types are not supported.");
            }
            ParameterizedType factoryParameterizedReturnType = (ParameterizedType) factoryMethod.getGenericReturnType();
            if (!Arrays.equals(factoryParameterizedReturnType.getActualTypeArguments(), factoryMethod.getTypeParameters())) {
                throw new JSONDeserializerException("Factory method " + factoryMethod.toGenericString() + " in "
                        + cls.getCanonicalName()
                        + " must return "
                        + new ResolvedParameterizedType(null, cls, factoryMethod.getTypeParameters()).toString()
                        + ".");
            }
        }
    }
}
