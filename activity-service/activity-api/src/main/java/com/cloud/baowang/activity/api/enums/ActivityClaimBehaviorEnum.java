package com.cloud.baowang.activity.api.enums;

public enum ActivityClaimBehaviorEnum {
    USER_SELF_CLAIM("用户自领", 1),
    SYSTEM_DISPATCH("系统派发", 2);

    private final String name;
    private final Integer code;

    ActivityClaimBehaviorEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
