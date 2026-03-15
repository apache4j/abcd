package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param merchant_modify_type 类型
 */
@Getter
@AllArgsConstructor
public enum MerchantModifyTypeEnums {
    MERCHANT_ACCOUNT_STATUS(0, "账号状态");
    private final Integer type;
    private final String name;
}
