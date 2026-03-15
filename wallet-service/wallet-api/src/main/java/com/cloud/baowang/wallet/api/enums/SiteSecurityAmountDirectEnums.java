package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收支类型
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityAmountDirectEnums {

    ADD("+", "收入"),
    SUB("-", "支出"),
    NONE("N", "无"),
    ;



    private final String code;
    private final String value;


}
