package com.cloud.baowang.play.api.enums.cq9;

import lombok.Getter;

@Getter
public enum CQ9CallEnums {
    BET("bet"),
    ENDROUND("endround"),
    DEBIT("debit"),
    CREDIT("credit"),
    ROLLIN("rollin"),
    ROLLOUT("rollout"),
    TAKEALL("takeall"),//refund
    REFUND("refund"),
    PAYOFF("payoff");

    private final String code;


    CQ9CallEnums(String code) {
        this.code = code;

    }
}
