package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum FreeGameChangeTypeEnum {
    USED(0, "用户使用"),
    ACTIVITY_ADD(1, "活动赠送"),
    CONFIG_ADD(2, "人工添加"),

    ;

    private final int code;
    private final String name;

    FreeGameChangeTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static FreeGameChangeTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        FreeGameChangeTypeEnum[] types = FreeGameChangeTypeEnum.values();
        for (FreeGameChangeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
