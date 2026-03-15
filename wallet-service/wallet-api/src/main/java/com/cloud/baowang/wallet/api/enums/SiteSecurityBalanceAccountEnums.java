package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账户保证金账户类型
 * 保证金
 * 冻结保证金
 * 透支额度
 * 剩余透支额度
 * 冻结透支额度
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityBalanceAccountEnums {

    AVAILABLE("AVAILABLE", "保证金"),
    FROZEN("FROZEN", "冻结保证金"),
    OVERDRAW("OVERDRAW", "透支额度"),
    OVERDRAW_FROZEN("OVERDRAW_FROZEN", "冻结透支额度"),
    OVERDRAW_AVAILABLE("OVERDRAW_AVAILABLE", "剩余透支额度")

    ;


    private final String code;
    private final String value;


}
