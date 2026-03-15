package com.cloud.baowang.activity.api.enums.redbag;

import lombok.Getter;

/**
 * 红包状态
 */
@Getter
public enum RedBagStatusEnum {
    // 定义枚举常量
    NOT_RECEIVE(0, "未发放"),
    RECEIVED(1, "已发放"),
    ;

    private final int status;
    private final String value;

    // 构造函数
    RedBagStatusEnum(int status, String value) {
        this.status = status;
        this.value = value;
    }
}
