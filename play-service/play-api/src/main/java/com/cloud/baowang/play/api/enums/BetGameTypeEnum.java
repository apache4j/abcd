package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum BetGameTypeEnum {

    FREE_SPIN("free_spin", "免费旋转");

    private final String code;
    private final String desc;

    BetGameTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
