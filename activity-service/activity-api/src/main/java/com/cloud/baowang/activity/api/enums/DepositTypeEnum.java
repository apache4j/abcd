package com.cloud.baowang.activity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值类型枚举
 */
@AllArgsConstructor
@Getter
public enum DepositTypeEnum {

    /**
     * 首充: 指第一次充值
     */
    FIRST_DEPOSIT(0, "首充"),

    /**
     * 次充: 指第二次充值
     */
    SECOND_DEPOSIT(1, "次充"),

    /**
     * 累充: 指累计充值
     */
    CUMULATIVE_DEPOSIT(2, "累充"),

    ONE_DEPOSIT(3, "单次");

    private final Integer value;
    private final String description;

}
