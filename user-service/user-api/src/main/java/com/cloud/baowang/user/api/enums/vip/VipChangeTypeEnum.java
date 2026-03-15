package com.cloud.baowang.user.api.enums.vip;

import lombok.Getter;

/**
 * @author mufan
 */
@Getter
public enum VipChangeTypeEnum {

    up(0, "升级"),
    down(1, "降级"),
    ;

    private final Integer code;
    private final String name;

    VipChangeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
