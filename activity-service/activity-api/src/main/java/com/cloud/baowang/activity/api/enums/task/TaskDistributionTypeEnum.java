package com.cloud.baowang.activity.api.enums.task;


/**
 * 派发方式
 */
public enum TaskDistributionTypeEnum {
    SELF_EXPIRE_INVALID(0, "玩家自领-过期作废"),

    NO_EXPIRE(1, "玩家自领-没有过期");

    private final Integer code;
    private final String name;

    TaskDistributionTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TaskDistributionTypeEnum fromCode(int code) {
        for (TaskDistributionTypeEnum type : TaskDistributionTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type code: " + code);
    }

    public static TaskDistributionTypeEnum fromName(String name) {
        for (TaskDistributionTypeEnum type : TaskDistributionTypeEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type name: " + name);
    }
}
