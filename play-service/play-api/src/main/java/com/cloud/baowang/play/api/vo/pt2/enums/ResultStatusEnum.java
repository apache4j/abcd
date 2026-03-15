package com.cloud.baowang.play.api.vo.pt2.enums;

import lombok.Getter;

@Getter
public enum ResultStatusEnum {
    FREESPIN2("ACCEPTED", "免费旋转奖金2"),
    POKER("REMOVED", "扑克奖金");

    private final String code;
    private final String desc;

    ResultStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static ResultStatusEnum fromCode(String code) {
        for (ResultStatusEnum reward : values()) {
            if (reward.code.equalsIgnoreCase(code)) {
                return reward;
            }
        }
        return null;
    }

    /**
     * 根据中文描述获取枚举
     */
    public static String getDescription(String description) {
        for (ResultStatusEnum reward : values()) {
            if (reward.desc.equals(description)) {
                return reward.desc;
            }
        }
        return "";
    }
}
