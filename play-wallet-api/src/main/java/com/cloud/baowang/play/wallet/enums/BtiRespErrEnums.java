package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum BtiRespErrEnums {
    // 成功
    SUCCESS(0, "成功"),
    CustomerNotFound(-2, "用户没找到"),
    TokenNotValid(-3, "token校验失败"),
    RestrictedCustomer(-6, "受限客户"),

    NoExistingSession(-23, "没有现有会话"),
    ;

    private final Integer code;
    private final String description;

    BtiRespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
