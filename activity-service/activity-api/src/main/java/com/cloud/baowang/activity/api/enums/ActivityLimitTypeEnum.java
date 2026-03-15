package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 转盘活动-转盘活动每位会员可领取次数上限
 */
@Getter
public enum ActivityLimitTypeEnum {

    // 定义枚举常量
    ALL(0, "全部会员"),
    VIP(1, "按VIP等级限制会员领取次数");

    private final Integer type;
    private final String value;

    // 构造函数
    ActivityLimitTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
