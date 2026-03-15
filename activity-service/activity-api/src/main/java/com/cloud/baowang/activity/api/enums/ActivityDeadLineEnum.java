package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 活动期限
 * system_param 中的 activity_deadline
 */
@Getter
public enum ActivityDeadLineEnum {

    // 定义枚举常量
    LIMITED_TIME(0, "限时"),
    LONG_TERM(1, "长期");

    private final Integer type;
    private final String value;

    // 构造函数
    ActivityDeadLineEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
