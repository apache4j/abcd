package com.cloud.baowang.activity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 兑换码枚举
 */
@Getter
@AllArgsConstructor
public enum ActivityRedemptionCodeEnum {

    ACTIVITY_REDEMPTION_CODE_CATEGORY_COMMON(0,"通用兑换码"),
    ACTIVITY_REDEMPTION_CODE_CATEGORY_UNIQUE(1,"唯一兑换码"),
    ACTIVITY_REDEMPTION_CODE_CONDITION_UNLIMIT_USER(1,"无限制用户"),
    ACTIVITY_REDEMPTION_CODE_CONDITION_DEPOSIT_USER(2,"存款用户"),
    ACTIVITY_REDEMPTION_CODE_CONDITION_ENABLE_DEPOSITE_DAY_USER(3,"当天存款用户-兑换码生效日进行存款的用户"),
    ACTIVITY_REDEMPTION_CODE_CONDITION_THREE_DAY_DEPOSIT_USER(4,"三天内存款用户");
    private  final Integer code;
    private final String message;



}
