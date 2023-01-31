package com.lkn.dag.handlers;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author xijiu
 * @since 2022/5/25 下午3:44
 */
@Slf4j
public class ReflectTools {
    public static Object get(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Reflect get filed failed, object {}, fieldName {}", object, fieldName, e);
            throw new RuntimeException(e);
        }
    }

    public static void set(Object object, String fieldName, Object fieldVal) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldVal);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Reflect set filed failed, object {}, fieldName {}, fieldVal {}", object, fieldName, fieldVal, e);
            throw new RuntimeException(e);
        }
    }
}
