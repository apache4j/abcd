package com.cloud.baowang.common.es.util;

import org.springframework.lang.Nullable;

/**
 * @Author: sheldon
 * @Date: 3/20/24 6:19 下午
 */
public class NumberUtil {

    public static int toInt(@Nullable final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

}
