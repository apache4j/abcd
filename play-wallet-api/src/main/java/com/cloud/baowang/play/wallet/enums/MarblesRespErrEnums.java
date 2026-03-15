package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum MarblesRespErrEnums {

    SUCCESS(0, "成功"),

    PLAYER_NOT_EXIST(504, "玩家不存在。"),

    PLAYER_INACTIVE(542, "玩家在停用状态"),

    INSUFFICIENT_AMOUNT(510, "余额不足"),

    TRANSACTIONID_IS_NOT_FOUND (545, "该笔交易不存在"),

    INVALID_TOKEN(531, "无效会话"),


    ;

    private final Integer code;
    private final String description;

    MarblesRespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
