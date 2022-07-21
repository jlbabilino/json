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
                    checkFactoryMethodConditions(method);
                    classModel.constructor = method;
                    break;
                }
            }
        }
        if (!Modifier.isAbstract(cls.getModifiers()) && !cls.isInterface()) {
            Constructor<?>[] constructors = cls.getDeclaredConstructors();
            for (Constructor<?> declaredConstructor : constructors) {
                if (declaredConstructor.isAnnotationPresent(DeserializedJSONConstructor.class)) {
                    checkZeroTypeParameters(declaredConstructor);
                    classModel.constructor = declaredConstructor;
                    break;
                }
            }
        }
        collectClassModel(cls, classModel);
        
        return classModel;
    }

    private static void collectClassModel(Class<?> cls, JSONClassModel classModel) {
        if (cls != null && cls != Object.class) {
            for (Class<?> superInterface : cls.getInterfaces()) {
                collectClassModel(superInterface, classModel);
            }
            collectClassModel(cls.getSuperclass(), classModel);
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
                if (addMethodToModel(clsMethod, classModel.serializedJSONEntryMethods, SerializedJSONEntry.class)) continue;
                if (addMethodToModel(clsMethod, classModel.serializedJSONObjectValueMethods, SerializedJSONObjectValue.class)) continue;
                if (addMethodToModel(clsMethod, classModel.serializedJSONArrayItemMethods, SerializedJSONArrayItem.class)) continue;
                if (addMethodToModel(clsMethod, classModel.deserializedJSONTargetMethods, DeserializedJSONTarget.class)) continue;
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

        @Override
        public String toString() {
            return annotation.toString() + " " + method.toGenericString();
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
            throw new JSONDeserializerException("Determiner " + System.lineSeparator() + System.lineSeparator()
                    + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + " must be a static method.");
        }
        if (determiner.getExceptionTypes().length > 1
                || (determiner.getExceptionTypes().length == 1
                && determiner.getExceptionTypes()[0] != JSONDeserializerException.class)) {
            throw new JSONDeserializerException("Determiner " + System.lineSeparator() + System.lineSeparator()
                    + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + " can throw up to one exception, and it can only throw exceptions of type \"JSONDeserializerException\"");
        }
        if (determiner.getTypeParameters().length > 0) {
            throw new JSONDeserializerException("Determiner" + System.lineSeparator() + System.lineSeparator()
                    + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + "should not be a generic method.");
        }
        if (!(determiner.getReturnType() == TypeMarker.class || determiner.getReturnType() == Class.class) ||
            !(determiner.getGenericReturnType() instanceof ParameterizedType)) {
            ResolvedParameterizedType recommendedType = new ResolvedParameterizedType(cls.getDeclaringClass(), Class.class, new Type[]{new ResolvedWildcardType(new Type[0], new Type[0])});
            throw new JSONDeserializerException("Determiner" + System.lineSeparator() + System.lineSeparator()
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
                throw new JSONDeserializerException("Determiner" + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must return Class<TYPE> or TypeMarker<TYPE> where TYPE is this type or a subtype.");
            }
        } else if (genericReturnType.getActualTypeArguments()[0] instanceof WildcardType) {
            WildcardType typeMarkerWildcard = (WildcardType) genericReturnType
                    .getActualTypeArguments()[0];
            Type wildcardUpperBound = typeMarkerWildcard.getUpperBounds()[0];
            if (!cls.isAssignableFrom(resolveClass(wildcardUpperBound))) {
                throw new JSONDeserializerException("Determiner" + System.lineSeparator() + System.lineSeparator()
                        + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must return Class<? extends TYPE> or TypeMarker<? extends TYPE> where TYPE is this type or a subtype.");
            }
        } else {
            throw new JSONDeserializerException("Determiner" + System.lineSeparator() + System.lineSeparator() 
                    + determiner.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + "must return TypeMarker<? extends TYPE>, Class<? extends TYPE>, TypeMarker<TYPE>, or Class<TYPE> where TYPE is this type or a subtype.");
        }
    }

    private static void checkFactoryMethodConditions(Method factoryMethod) throws JSONDeserializerException {
        Class<?> cls = factoryMethod.getDeclaringClass();
        Type[] clsTypeParameters = cls.getTypeParameters();
        int factoryMethodModifiers = factoryMethod.getModifiers();
        if (!Modifier.isStatic(factoryMethodModifiers)) {
            throw new JSONDeserializerException("Factory method"
                    + System.lineSeparator() + System.lineSeparator()
                    + factoryMethod.toGenericString()
                    + System.lineSeparator() + System.lineSeparator() + "must be static.");
        }
        if (factoryMethod.getTypeParameters().length != clsTypeParameters.length) {
            throw new JSONDeserializerException("Factory method" + System.lineSeparator() + System.lineSeparator()
                    + factoryMethod.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + "must be a generic method with the same number of type parameters as its declaring class:"
                    + System.lineSeparator() + System.lineSeparator() + cls.toGenericString());
        }
        if (factoryMethod.getReturnType() != cls) {
            throw new JSONDeserializerException("Factory method"
                    + System.lineSeparator() + System.lineSeparator()
                    + factoryMethod.toGenericString()
                    + "must return" + System.lineSeparator() + System.lineSeparator()
                    + cls.getCanonicalName());
        }
        if (clsTypeParameters.length > 0) {
            if (!(factoryMethod.getGenericReturnType() instanceof ParameterizedType)) {
                throw new JSONDeserializerException("Factory method"
                        + System.lineSeparator() + System.lineSeparator() + factoryMethod.toGenericString()
                        + System.lineSeparator() + System.lineSeparator()
                        + "must not return a raw type since raw types are not supported.");
            }
            ParameterizedType factoryParameterizedReturnType = (ParameterizedType) factoryMethod.getGenericReturnType();
            if (!Arrays.equals(factoryParameterizedReturnType.getActualTypeArguments(), factoryMethod.getTypeParameters())) {
                throw new JSONDeserializerException("Factory method" + System.lineSeparator() + System.lineSeparator()
                        + factoryMethod.toGenericString() + System.lineSeparator() + System.lineSeparator()
                        + "must return" + System.lineSeparator() + System.lineSeparator()
                        + new ResolvedParameterizedType(null, cls, factoryMethod.getTypeParameters()).toString());
            }
        }
    }

    private static void checkZeroTypeParameters(Executable executable) throws JSONDeserializerException {
        if (executable.getTypeParameters().length != 0) {
            throw new JSONDeserializerException("Could not invoke executable"
                    + System.lineSeparator() + System.lineSeparator()
                    + executable.toGenericString() + System.lineSeparator() + System.lineSeparator()
                    + "since generic executables beyond factory methods are not supported for JSON serialization/deserialization.");
        }
    }
}
