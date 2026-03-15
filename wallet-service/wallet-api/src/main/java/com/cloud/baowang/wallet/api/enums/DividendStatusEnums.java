package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param dividend_status code
 */
@AllArgsConstructor
@Getter
public enum DividendStatusEnums {

    SUCCESS(1, "成功"),
    FAILURE(2, "失败"),
    IN_PROGRESS(3, "处理中"),
    NOT_RECEIVED(4, "未领取"),
    EXPIRED(5, "已过期");

    private final Integer code;
    private final String value;


}
