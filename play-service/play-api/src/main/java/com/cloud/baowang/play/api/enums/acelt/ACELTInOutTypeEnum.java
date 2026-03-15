package com.cloud.baowang.play.api.enums.acelt;

import java.util.Objects;

public enum ACELTInOutTypeEnum {

    IN(1, "收入"),
    OUT(2, "支出");

    private final Integer code;       // 收入或支出的代码
    private final String description; // 收入或支出的描述

    // 构造函数
    ACELTInOutTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取收入或支出的代码
    public Integer getCode() {
        return code;
    }

    // 获取收入或支出的描述
    public String getDescription() {
        return description;
    }

    // 通过代码获取对应的枚举
    public static ACELTInOutTypeEnum fromCode(Integer code) {
        for (ACELTInOutTypeEnum type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }
}
