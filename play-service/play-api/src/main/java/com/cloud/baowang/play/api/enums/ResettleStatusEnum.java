package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum ResettleStatusEnum {
    DELAY(1, "重算中"),
    RESETTLE(2, "已重算"),
    NOT_SETTLE(3, "未重算"),
    NO_SETTLE(4, "不重算"); //不涉及重算的单

    private final int code;
    private final String name;

    ResettleStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ResettleStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ResettleStatusEnum[] types = ResettleStatusEnum.values();
        for (ResettleStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
