package com.cloud.baowang.play.api.vo.sexy.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum VoidTypeEnum {
    TIP(2, "游戏无效、现场操作问题"),
    CANCEL_TIP(9, "作弊");

    private final Integer code;
    private final String description;

    VoidTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 通过 code 获取枚举
     */
    public static VoidTypeEnum fromCode(Integer code) {
        for (VoidTypeEnum type : values()) {
            if (Objects.equals(type.code, code)) {
                return type;
            }
        }
        return null;
    }
}
