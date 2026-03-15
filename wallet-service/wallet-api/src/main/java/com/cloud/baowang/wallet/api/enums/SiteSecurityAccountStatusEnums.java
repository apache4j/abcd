package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证金账户状态 1:正常 2:预警 3:透支
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityAccountStatusEnums {

    NORMAL(1, "正常"),
    ALARM(2, "预警"),
    OVERDRAW(3, "透支"),
    ;
    private final Integer code;
    private final String value;


}
