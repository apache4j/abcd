package com.cloud.baowang.user.api.enums;

import lombok.Getter;

@Getter
public enum SendObjectEnum {
    ALL(0,"全部"),
    MEMBER(1,"会员"),
    TERMINAL(2,"终端");



    private final int code;
    private final String name;

    SendObjectEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SendObjectEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        SendObjectEnum[] types = SendObjectEnum.values();
        for (SendObjectEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
