package com.cloud.baowang.play.game.db.evg.enums;

public enum PlayTypeEnum {
    UN_KNOW(0, "普通旋转"),
    NORMAL_SPIN(1, "普通旋转"),
    FREE_GAME(2, "免费游戏"),
    SPECIAL_SPIN(3, "特殊旋转"),
    PROMOTION(5, "优惠活动"),
    MULTI_BET(6, "多次下注");

    private final int code;
    private final String description;

    PlayTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PlayTypeEnum fromCode(int code) {
        for (PlayTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
       return UN_KNOW;
    }
}
