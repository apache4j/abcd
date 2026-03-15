package com.cloud.baowang.common.core.utils;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static void setField(Object target, String name, Object value) {
        Assert.notNull(target, "Target object must not be null");
        Field field = ReflectionUtils.findField(target.getClass(), name);
        if (field == null) {
            throw new IllegalStateException("Could not find field [" + name + "] on target [" + target + "]");
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, value);
    }

    public static Object getField(Object target, String name) {
        Assert.notNull(target, "Target object must not be null");
        Field field = ReflectionUtils.findField(target.getClass(), name);
        if (field == null) {
            throw new IllegalStateException("Could not find field [" + name + "] on target [" + target + "]");
        }
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, target);
    }
}
