package com.cloud.baowang.activity.api.enums;


import java.util.Objects;

/**
 * 每日竞赛上榜状态
 */
public enum ActivityCompletionUserStatusTypeEnum {
    NOT_RANKED(0, "未上榜"),  // 未上榜
    FIRST_PLACE(1, "第一名"), // 第一名
    RANKED(2, "已上榜");      // 已上榜

    private final Integer code;
    private final String name;

    ActivityCompletionUserStatusTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ActivityCompletionUserStatusTypeEnum fromCode(Integer code) {
        for (ActivityCompletionUserStatusTypeEnum type : ActivityCompletionUserStatusTypeEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }

    public static ActivityCompletionUserStatusTypeEnum fromName(String name) {
        for (ActivityCompletionUserStatusTypeEnum type : ActivityCompletionUserStatusTypeEnum.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid distribution type name: " + name);
    }
}
