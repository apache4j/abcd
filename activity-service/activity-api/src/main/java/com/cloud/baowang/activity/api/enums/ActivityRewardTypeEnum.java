package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 转盘活动-奖励
 * system_param 中 activity_reward_type
 */
@Getter
public enum ActivityRewardTypeEnum {

    // 定义枚举常量
    AMOUNT(1, "金额"),
    physical(2, "实物");

    private final int type;
    private final String value;

    // 构造函数
    ActivityRewardTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
