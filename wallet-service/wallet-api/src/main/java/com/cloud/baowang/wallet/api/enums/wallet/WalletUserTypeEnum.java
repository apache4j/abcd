package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum WalletUserTypeEnum {
    USER("USER", "会员"),
    AGENT("AGENT", "代理"),
    ;

    private final String code;
    private final String name;

    WalletUserTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        WalletUserTypeEnum[] types = WalletUserTypeEnum.values();
        for (WalletUserTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static List<WalletUserTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
