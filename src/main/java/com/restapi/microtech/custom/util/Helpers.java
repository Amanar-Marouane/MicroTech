package com.restapi.microtech.custom.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Helpers {
    public static void setFieldIfPresent(Object entity, Class<? extends Annotation> annotation, Object value) {
        Class<?> current = entity.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    field.setAccessible(true);
                    try {
                        field.set(entity, value);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
            current = current.getSuperclass();
        }
    }
}
