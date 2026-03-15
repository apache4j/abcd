package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

@Getter
public enum AwardTypeEnum {

    WEEK("0", "周红包"),
    MONTH("1", "月红包"),
    ;
    private final String code;
    private final String name;

    AwardTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AwardTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AwardTypeEnum[] types = AwardTypeEnum.values();
        for (AwardTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
