package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum SportRecommendStatusEnum {

    NOT_STARTED(0, "未开赛"),
    ALREADY_STARTED(1, "已开赛");

    private final Integer code;
    private final String name;

    SportRecommendStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SportRecommendStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        SportRecommendStatusEnum[] types = SportRecommendStatusEnum.values();
        for (SportRecommendStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
