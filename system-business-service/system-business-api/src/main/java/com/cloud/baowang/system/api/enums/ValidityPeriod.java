package com.cloud.baowang.system.api.enums;

import lombok.Getter;

@Getter
public enum ValidityPeriod {
    LIMIT_TIME(0, "限时"),

    PERMANENT(1, "永久")
    ;

    private Integer code;

    private String name;

    ValidityPeriod(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
