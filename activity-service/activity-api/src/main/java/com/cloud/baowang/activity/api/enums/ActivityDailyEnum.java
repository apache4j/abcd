package com.cloud.baowang.activity.api.enums;

public enum ActivityDailyEnum {
    ROBOT(0, "机器人"),
    REAL_USER(1, "真实用户");

    private final int code;
    private final String name;

    ActivityDailyEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    // 通过 code 获取对应的枚举
    public static ActivityDailyEnum fromCode(int code) {
        for (ActivityDailyEnum activity : ActivityDailyEnum.values()) {
            if (activity.getCode() == code) {
                return activity;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
