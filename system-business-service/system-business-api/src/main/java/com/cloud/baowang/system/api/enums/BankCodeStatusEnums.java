package com.cloud.baowang.system.api.enums;

import lombok.Getter;

@Getter
public enum BankCodeStatusEnums {
    ALL("0","全部"),
    FULL("1", "全量配置"),

    MISSING("2", "非全量配置"),
    NONE("3", "未配置"),
    ;

    private String code;

    private String name;

    BankCodeStatusEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(String code) {
        if (null == code) {
            return NONE.name;
        }
        BankCodeStatusEnums[] types = BankCodeStatusEnums.values();
        for (BankCodeStatusEnums type : types) {
            if (code.equals(type.getCode())) {
                return type.name;
            }
        }
        return NONE.name;
    }
}
