package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 长链接/短链接类型枚举
 */
@AllArgsConstructor
@Getter
public enum ShortORLongUrlEnums {
    SHORT_URL(0, "短链接"),
    LONG_URL(1, "长链接"),
    ;
    private final Integer type;
    private final String msg;
}
