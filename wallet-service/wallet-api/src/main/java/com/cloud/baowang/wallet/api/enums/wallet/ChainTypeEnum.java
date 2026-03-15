package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

@Getter
public enum ChainTypeEnum {

    TRON("TRON", "TRON"),
    ETH("ETH", "ETH"),
    ;
    private final String code;
    private final String name;

    ChainTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ChainTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        ChainTypeEnum[] types = ChainTypeEnum.values();
        for (ChainTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
