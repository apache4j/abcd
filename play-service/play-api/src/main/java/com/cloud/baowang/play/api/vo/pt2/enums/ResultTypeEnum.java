package com.cloud.baowang.play.api.vo.pt2.enums;

public enum ResultTypeEnum {
    FREESPIN2("ACCEPTED", "免费旋转奖金2"),
    POKER("REMOVED", "扑克奖金");

    private final String code;
    private final String desc;

    ResultTypeEnum(String code, String desc) {
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
    public static ResultTypeEnum fromCode(String code) {
        for (ResultTypeEnum reward : values()) {
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
        for (ResultTypeEnum reward : values()) {
            if (reward.desc.equals(description)) {
                return reward.desc;
            }
        }
        return "";
    }
}
