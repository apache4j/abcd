package com.cloud.baowang.common.core.utils;


import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import org.springframework.util.ObjectUtils;

public class AssertUtil {

    public static void isTrue(Boolean condition, String msg) {
        if (condition) {
            throw new BaowangDefaultException(msg);
        }
    }

    public static void isEmptyObject(Object object, String msg) {
        if (ObjectUtils.isEmpty(object)) {
            throw new BaowangDefaultException(msg);
        }
    }

    public static void isNotEmptyObject(Object object, String msg) {
        if (!ObjectUtils.isEmpty(object)) {
            throw new BaowangDefaultException(msg);
        }
    }
}
