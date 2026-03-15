package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 签到-奖励
 * system_param 中 checkIn_reward_type
 */
@Getter
public enum CheckInRewardTypeEnum {

    // 定义枚举常量
    AMOUNT("AMOUNT", "金额"),
    FREE_WHEEL("FREE_WHEEL", "免费旋转"),
    SPIN_WHEEL("SPIN_WHEEL", "转盘");

    private final String type;
    private final String value;

    // 构造函数
    CheckInRewardTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public static CheckInRewardTypeEnum of(String type) {
        for (CheckInRewardTypeEnum rewardType : values()) {
            if (rewardType.getType().equalsIgnoreCase(type)) {
                return rewardType;
            }
        }
        return null;
    }
}
