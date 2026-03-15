package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum AgentGameRecordOrderEnum {

    BET_TIME_DESC(1, "投注时间近至远"),
    WIN_LOSS_AMOUNT_DESC(2, "输赢金额高至低"),
    BET_AMOUNT_DESC(3, "投注金额高至低"),
    ;

    private final int code;
    private final String name;

    AgentGameRecordOrderEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentGameRecordOrderEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentGameRecordOrderEnum[] types = AgentGameRecordOrderEnum.values();
        for (AgentGameRecordOrderEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
