package com.cloud.baowang.activity.api.enums.redbag;

import lombok.Getter;

@Getter
public enum RedBagTypeEnum {
    // 定义枚举常量
    FIXED(1, "固定金额"),
    RANDOM(2, "固定金额");

    private final int type;
    private final String value;

    // 构造函数
    RedBagTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    // 根据值获取枚举实例
    public static RedBagTypeEnum of(Integer type) {
        for (RedBagTypeEnum bagTypeEnum : RedBagTypeEnum.values()) {
            if (bagTypeEnum.type ==(type)) {
                return bagTypeEnum;
            }
        }
        return null;
    }
}
