package com.cloud.baowang.activity.api.enums.task;


/**
 * 任务了领取放手 0
 */
public enum TaskReceiveStatusEnum {
    NOT_ELIGIBLE(3, "未达到领取条件"),
    ELIGIBLE(0, "可领取"),
    CLAIMED(1, "已领取"),
    EXPIRED(2, "奖励已过期");

    private final Integer code;
    private final String name;

    TaskReceiveStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TaskReceiveStatusEnum fromCode(int code) {
        for (TaskReceiveStatusEnum type : TaskReceiveStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type code: " + code);
    }

    public static TaskReceiveStatusEnum fromName(String name) {
        for (TaskReceiveStatusEnum type : TaskReceiveStatusEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type name: " + name);
    }
}
