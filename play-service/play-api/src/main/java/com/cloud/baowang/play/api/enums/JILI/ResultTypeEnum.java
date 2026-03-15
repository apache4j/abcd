package com.cloud.baowang.play.api.enums.JILI;

public enum ResultTypeEnum {
    WIN, BET_WIN,BET_LOSE,LOSE,END;

    public static ResultTypeEnum typeEnum(String code) {
        for (ResultTypeEnum typeEnum : ResultTypeEnum.values()) {
            if (typeEnum.name().equalsIgnoreCase(code)) {
                return typeEnum;
            }
        }
        return END;
    }
}
