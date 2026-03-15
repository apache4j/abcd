package com.cloud.baowang.play.wallet.vo.req.pt2.enums;

import lombok.Getter;

@Getter
public enum RewardEnum {

    FREESPIN("FREESPIN", "免费旋转奖金"),
    GOLDENCHIP("GOLDENCHIP", "黄金筹码奖金"),
    PREWAGER("PREWAGER", "预赌奖金"),
    AFTERWAGER("AFTERWAGER", "赌注奖金"),
    CASH("CASH", "现金奖金"),
    FREESPIN2("FREESPIN2", "免费旋转奖金2"),
    POKER("POKER", "扑克奖金");

    private final String code;
    private final String desc;

    RewardEnum(String code, String desc) {
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
    public static RewardEnum fromCode(String code) {
        for (RewardEnum reward : values()) {
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
        for (RewardEnum reward : values()) {
            if (reward.desc.equals(description)) {
                return reward.desc;
            }
        }
        return "";
    }
}

