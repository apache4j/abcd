package com.cloud.baowang.activity.api.enums;


import java.util.Objects;

/**
 * 派发方式
 *
 * system_param 中的  activity_distribution_type
 */
public enum ActivityDistributionTypeEnum {
    SELF_EXPIRE_INVALID(0, "玩家自领-过期作废"),

    SELF_EXPIRE_AUTO(1, "玩家自领-过期自动派发"),

    IMMEDIATE(2, "立即派发");

    private final Integer code;
    private final String name;

    ActivityDistributionTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ActivityDistributionTypeEnum fromCode(Integer code) {
        for (ActivityDistributionTypeEnum type : ActivityDistributionTypeEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }

    public static ActivityDistributionTypeEnum fromName(String name) {
        for (ActivityDistributionTypeEnum type : ActivityDistributionTypeEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type name: " + name);
    }
}
