package com.cloud.baowang.play.game.sa;


import lombok.Getter;

/**
 * SA 奖励
 */
@Getter
public enum SAAdjustmentTypeEnum {
    GIFT(2, "赠送奖赏-扣费"),
    CANCEL(3, "取消奖赏-加款");
    private final Integer code;
    private final String description;

    SAAdjustmentTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    public static SAAdjustmentTypeEnum byCode(Integer code) {
        for (SAAdjustmentTypeEnum tmp : SAAdjustmentTypeEnum.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

}
