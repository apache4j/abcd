package com.cloud.baowang.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * vip 币种-手续费类型枚举,使用 system_param fee_type
 */
@AllArgsConstructor
@Getter
public enum VipCurrencyFeeTypeEnum {

    FIXED_AMOUNT(1, "固定金额"),
    PERCENTAGE(0, "百分比");

    private final Integer feeType;
    private final String name;

}
