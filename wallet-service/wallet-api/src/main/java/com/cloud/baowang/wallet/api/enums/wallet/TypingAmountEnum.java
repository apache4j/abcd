package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

public enum TypingAmountEnum {


    ADD("1", "增加"),
    SUBTRACT("2", "减少"),
    ;

    private String code;
    private String name;

    TypingAmountEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TypingAmountEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TypingAmountEnum[] types = TypingAmountEnum.values();
        for (TypingAmountEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<TypingAmountEnum> getList() {
        return Arrays.asList(values());
    }
}
