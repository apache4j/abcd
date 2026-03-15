package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum ClassifyEnum {
    NOT_SETTLE(0, "未结算"),
    SETTLED(1, "已结算"),
    CANCEL(2, "已取消"),
    RESETTLED(3, "重结算");

    private final Integer code;
    private final String name;

    ClassifyEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ClassifyEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ClassifyEnum[] types = ClassifyEnum.values();
        for (ClassifyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
