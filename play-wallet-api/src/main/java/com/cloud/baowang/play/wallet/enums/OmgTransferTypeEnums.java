package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum OmgTransferTypeEnums {

    BET(1, "游戏下注"),
    CANCEL(2, "取消下注"),
    PAYOUT(3, "游戏返奖"),
    END(4, "验证对局结束"),
    LUCKWIN(5, "LuckWin游戏宝箱下发奖励"),
    Future(6, "Future游戏持仓费用扣减");
    private final Integer code;
    private final String description;

    OmgTransferTypeEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static OmgTransferTypeEnums fromCode(Integer code) {
        for (OmgTransferTypeEnums type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }

}
