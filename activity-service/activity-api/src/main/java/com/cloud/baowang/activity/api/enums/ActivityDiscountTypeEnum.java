package com.cloud.baowang.activity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动优惠方式枚举类
 * system_param 中的  activity_discount_type
 */
@Getter
@AllArgsConstructor
public enum ActivityDiscountTypeEnum {

    PERCENTAGE(0, "百分比"),
    FIXED_AMOUNT(1, "固定金额");

    private final Integer type;
    private final String description;


    // 根据 type 查询枚举的方法
    public static ActivityDiscountTypeEnum fromType(Integer type) {
        for (ActivityDiscountTypeEnum value : ActivityDiscountTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
