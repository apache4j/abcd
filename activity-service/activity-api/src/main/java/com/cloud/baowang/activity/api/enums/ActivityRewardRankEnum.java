package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 转盘活动-奖励
 */
@Getter
public enum ActivityRewardRankEnum {

    // 定义枚举常量
    BRONZE(1, "青铜"),
    SILVER(2, "白银"),
    GOLD(3, "黄金及以上");

    private final Integer type;
    private final String value;

    // 构造函数
    ActivityRewardRankEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
