package com.nemesiss.dev.oauthplayground.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

@Slf4j
public class AnnotationGetter {

    public static <T extends Annotation> T GetAnnotationOnField(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T GetAnnotationOnMethod(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T GetAnnotationOnClass(Class<?> clazz, Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }


    public static String GetJsonPropertyValue(Field field, String defaultValue) {
        JsonProperty jsonProperty = GetAnnotationOnField(field, JsonProperty.class);
        return Optional.of(jsonProperty).map(JsonProperty::value).orElse(defaultValue);
    }

    public static String GetJsonPropertyValue(Class<?> clazz, String fieldName, String defaultValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return GetJsonPropertyValue(field, defaultValue);
        } catch (NoSuchFieldException e) {
            log.error("Cannot find field name: " + e.getMessage());
            return defaultValue;
        }
    }
}
