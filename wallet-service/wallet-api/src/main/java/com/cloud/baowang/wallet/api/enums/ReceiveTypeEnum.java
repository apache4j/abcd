package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ReceiveTypeEnum {

    MANUAL_RECEIVE(0, "手动领取"),

    AUTOMATIC_RECEIVE(1, "自动领取"),
    ;

    private final int code;

    private final String value;


}
