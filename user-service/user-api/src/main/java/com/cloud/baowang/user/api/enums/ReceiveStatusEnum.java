package com.cloud.baowang.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 使用活动领取状态 system_param activity_receive_status
 */
@AllArgsConstructor
@Getter
public enum ReceiveStatusEnum {
    NOT_RECEIVED(0, "未领取"),
    RECEIVED(1, "已领取"),
    EXPIRED(2, "已过期");
    private final int code;
    private final String value;

}
