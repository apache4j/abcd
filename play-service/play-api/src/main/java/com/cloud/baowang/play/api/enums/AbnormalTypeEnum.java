package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum AbnormalTypeEnum {
    DELAY(0, "延迟单"),
    RESETTLE(1, "重算单"),
    CHANGE(2, "更改单");

    private final int code;
    private final String name;

    AbnormalTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AbnormalTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AbnormalTypeEnum[] types = AbnormalTypeEnum.values();
        for (AbnormalTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
