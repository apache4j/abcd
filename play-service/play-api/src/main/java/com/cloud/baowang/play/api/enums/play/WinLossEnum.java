package com.cloud.baowang.play.api.enums.play;

import lombok.Getter;

@Getter
public enum WinLossEnum {

    WIN(0, "赢"),
    TIE(1, "和"),
    LOSS(2, "输");

    private final int code;
    private final String name;

    WinLossEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WinLossEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        WinLossEnum[] types = WinLossEnum.values();
        for (WinLossEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
