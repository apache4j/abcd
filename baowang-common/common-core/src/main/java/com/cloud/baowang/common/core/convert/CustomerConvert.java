package com.cloud.baowang.common.core.convert;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

import static cn.hutool.core.util.ReflectUtil.getFields;

/**
 * @Author 小智
 * @Date 2/5/23 6:49 PM
 * @Version 1.0
 */
@Slf4j
public class CustomerConvert {

    /**
     * 类型转换
     *
     * @param obj
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object convert(final Object obj, final Class<?> clazz) {
        Object targetObj = null;
        try {
            targetObj = clazz.getDeclaredConstructor().newInstance();
            Class<?> originClass = obj.getClass();
            Field[] fields = getFields(originClass);

            for (Field field : fields) {
                if (notValidField(field) || isStaticModifier(field)) {
                    continue;
                }
                field.setAccessible(true);
                Object fileAtr = field.get(obj);

                if (Arrays.stream(clazz.getDeclaredFields()).anyMatch(item ->
                        item.getName().equals(field.getName()))) {
                    Field field1 = clazz.getDeclaredField(field.getName());
                    field1.setAccessible(true);
                    // 转换对应类型
                    if (null != fileAtr) {
                        Object convert = ConvertUtils.convert(fileAtr, field1.getType());
                        field1.set(targetObj, convert);
                    }
                }

            }
        } catch (Exception e) {
            log.error("convert have error source obj: {}, target obj: {} ", obj, clazz.getName(), e);
        }
        return targetObj;
    }

    private static boolean notValidField(final Field field) {
        return Objects.isNull(field);
    }

    private static boolean isStaticModifier(final Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
}
