package com.cloud.baowang.play.game.sh.enums;

import lombok.Getter;

/**
 * 神话投注订单状态
 */
@Getter
public enum ShOrderStatusEnum {
    ALREADY_SETTLED(-2, "打赏"),//代表已经扣钱,
    INITIAL(-1, "初始状态"),  //视讯不会返回这个状态
    NOT_SETTLEMENT(0, "未结算"),
    SETTLEMENT(1, "已结算"),
    CANCEL(2, "取消"),
    RECALCULATE(3, "重算"),
    ;

    private final Integer code;
    private final String name;

    ShOrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}