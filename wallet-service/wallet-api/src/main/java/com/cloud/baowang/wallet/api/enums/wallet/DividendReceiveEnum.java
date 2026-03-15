package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

@Getter
public enum DividendReceiveEnum {

    SUCCESS("0", "成功"),
    FAIL("1", "失败"),
    PROCESS("2", "处理中"),
    UN_RECEIVE("3", "未领取"),
    EXPIRED("4", "已过期"),
    ;

    private final String code;
    private final String name;

    DividendReceiveEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DividendReceiveEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DividendReceiveEnum[] types = DividendReceiveEnum.values();
        for (DividendReceiveEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
