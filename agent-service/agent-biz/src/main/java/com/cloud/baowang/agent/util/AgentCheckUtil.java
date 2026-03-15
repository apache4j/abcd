package com.cloud.baowang.agent.util;

import java.math.BigDecimal;

public class AgentCheckUtil {

    /**
     * 判断是不是整数形式
     */
    public static boolean isInteger(BigDecimal number) {
        return new BigDecimal(number.intValue()).compareTo(number) == 0;
    }


}
