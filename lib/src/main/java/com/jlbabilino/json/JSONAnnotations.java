package com.jlbabilino.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class JSONAnnotations {
    
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
            } else {
                return getJSONAnnotation(annotationClass, cls.getSuperclass(), methodName, parameterTypes);
            }
        } catch (NoSuchMethodException e) {
            return getJSONAnnotation(annotationClass, cls.getSuperclass(), methodName, parameterTypes);
        }
    }

    static <A extends Annotation> boolean isJSONAnnotationPresent(Class<A> annotationClass, Method method) {
        return getJSONAnnotation(annotationClass, method) != null;
    }
}