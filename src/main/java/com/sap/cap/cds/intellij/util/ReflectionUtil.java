package com.sap.cap.cds.intellij.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static Object getFieldValue(Object obj, String fieldName, @Nullable Class<?> superclass) throws NoSuchFieldException, IllegalAccessException {
        Field field = superclass == null ? obj.getClass().getDeclaredField(fieldName) : superclass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

}
