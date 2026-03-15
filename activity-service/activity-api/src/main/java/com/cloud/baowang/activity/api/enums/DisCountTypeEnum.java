package com.cloud.baowang.activity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠方式
 * 优惠方式 0:阶梯次数 1:固定次数
 * system_param 中的 discount_type
 */
@AllArgsConstructor
@Getter
public enum DisCountTypeEnum {

    /**
     * 阶梯次数
     */
    STAIR(0, "阶梯次数"),

    /**
     * 固定次数
     */
    FIX(1, "固定次数");



    private final int value;
    private final String description;

}
