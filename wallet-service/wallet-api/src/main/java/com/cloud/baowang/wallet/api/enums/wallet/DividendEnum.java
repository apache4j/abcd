package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

@Getter
public enum DividendEnum {

    ACTIVE("0", "活动优惠"),
    VIP("1", "VIP优惠"),
    DIVIDEND("2", "线下红利"),
    REBATE("3", "返水"),
    ;

    private final String code;
    private final String name;

    DividendEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DividendEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DividendEnum[] types = DividendEnum.values();
        for (DividendEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
