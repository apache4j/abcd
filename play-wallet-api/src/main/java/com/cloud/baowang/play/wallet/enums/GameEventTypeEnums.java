package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum GameEventTypeEnums {

    BET(1, "投注"),
    PAYOUT(2, "派彩"),
    CANCEL(3, "撤单"),
    ACTIVITY(4, "活动");

    private final Integer code;
    private final String description;

    GameEventTypeEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
