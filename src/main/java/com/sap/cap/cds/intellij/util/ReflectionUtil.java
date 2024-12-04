package com.sap.cap.cds.intellij.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

}
