package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 转盘活动-奖励-转盘活动奖励次数来源
 */
@Getter
public enum ActivityOperationTypeEnum {

    // 定义枚举常量
    DECREASE(0, "减少"),
    INCREASE(1, "增加");

    private final Integer type;
    private final String value;

    // 构造函数
    ActivityOperationTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
