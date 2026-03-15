package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 保证金开启状态 0:未开启 1:已开启
 */
@AllArgsConstructor
@Getter
public enum SiteSecurityStatusEnums {

    CLOSE(0, "未开启"),
    OPEN(1, "已开启"),
    ;
    private final Integer code;
    private final String value;


}
