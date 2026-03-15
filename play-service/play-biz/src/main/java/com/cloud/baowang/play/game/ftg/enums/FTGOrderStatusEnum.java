package com.cloud.baowang.play.game.ftg.enums;

import lombok.Getter;

/**
 * 神话投注订单状态
 */
@Getter
public enum FTGOrderStatusEnum {
    /**
     * 没有结果
     */
    N("N", "没有结果"),

    /**
     * 赢
     * 定义：派彩金额减去下注金额大于等于0，表示为赢
     */
    W("W", "赢"),

    /**
     * 输
     * 定义：派彩金额减去下注金额小于0，表示为输
     */
    L("L", "输"),

    /**
     * 注销
     */
    C("C", "注销");;

    private final String code;
    private final String name;

    FTGOrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}