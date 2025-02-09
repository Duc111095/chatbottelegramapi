package com.ducnh.chatbotapi.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ReflectUtils {
    public static List<Field> getAllFieldsList(final Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(fields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    @SneakyThrows
    public static <T> Object getProperty(T bean, String propertyName) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd != null && pd.getReadMethod() != null) {
            return pd.getReadMethod().invoke(bean);
        }
        return null;
    }

    @SneakyThrows
    public static <T> Object setProperty(Object bean, String propertyName, Object value) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd != null && pd.getWriteMethod() != null) {
            return pd.getWriteMethod().invoke(bean, value);
        }
        return null;
    }
}
