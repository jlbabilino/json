package com.jlbabilino.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;

final class JSONAnnotations {

    static final Comparator<SerializedJSONObjectValue> SERIALIZED_JSON_OBJECT_VALUE_COMPARATOR =
            (annotation1, annotation2) -> annotation1.key().compareTo(annotation2.key());
    static final Comparator<SerializedJSONArrayItem> SERIALIZED_JSON_ARRAY_ITEM_COMPARATOR =
            (annotation1, annotation2) -> annotation1.index() - annotation2.index();
    
    private JSONAnnotations() {
    }

    static <A extends Annotation> A getJSONAnnotation(Class<A> annotationClass, Method method) {
        return getJSONAnnotation(annotationClass, method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    private static <A extends Annotation> A getJSONAnnotation(Class<A> annotationClass, Class<?> cls, String methodName, Class<?>[] parameterTypes) {
        if (!cls.isAnnotationPresent(JSONSerializable.class)) {
            return null;
        }
        try {
            Method method = cls.getDeclaredMethod(methodName, parameterTypes);
            if (method.isAnnotationPresent(annotationClass)) {
                return method.getAnnotation(annotationClass);
            } else if (cls.getSuperclass() != null) {
                return getJSONAnnotation(annotationClass, cls.getSuperclass(), methodName, parameterTypes);
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return getJSONAnnotation(annotationClass, cls.getSuperclass(), methodName, parameterTypes);
        }
    }

    static <A extends Annotation> boolean isJSONAnnotationPresent(Class<A> annotationClass, Method method) {
        return getJSONAnnotation(annotationClass, method) != null;
    }
}