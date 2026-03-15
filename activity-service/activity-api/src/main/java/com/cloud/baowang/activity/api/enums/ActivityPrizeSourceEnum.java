package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 转盘活动-奖励-转盘活动奖励次数来源
 */
@Getter
public enum ActivityPrizeSourceEnum {

    // 定义枚举常量
    DEPOSIT("1", "存款赠送"),
    BET("2", "流水赠送"),
    VIP("3", "vip晋级"),
    CHECKIN("4", "签到活动赠送"),
    ;

    private final String type;
    private final String value;

    // 构造函数
    ActivityPrizeSourceEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
