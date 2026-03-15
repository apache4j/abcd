package com.cloud.baowang.activity.api.enums.redbag;

import lombok.Getter;

/**
 * 红包雨场次状态枚举
 */
@Getter
public enum RedBagSessionStatusEnum {
    // 定义枚举常量
    NOT_START(0, "未开始"),
    PROGRESS(1, "进行中"),
    END(2, "已结束");

    private final int status;
    private final String value;

    // 构造函数
    RedBagSessionStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }

    // 根据值获取枚举实例
    public static RedBagSessionStatusEnum of(int type) {
        for (RedBagSessionStatusEnum bagTypeEnum : RedBagSessionStatusEnum.values()) {
            if (bagTypeEnum.status == type) {
                return bagTypeEnum;
            }
        }
        return null;
    }
}
