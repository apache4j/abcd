package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 免费活动-发送状态
 * free_game_send_status
 */
@Getter
public enum FreeGameSendStatusEnum {

    // 定义枚举常量
    SENDING(0, "发送中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private final Integer type;
    private final String value;

    // 构造函数
    FreeGameSendStatusEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
