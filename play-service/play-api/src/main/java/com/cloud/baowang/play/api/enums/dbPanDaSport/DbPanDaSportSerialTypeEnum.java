package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportSerialTypeEnum {

    SINGLE(1, "单关"),
    TWO_2_1(2001, "2串1"),
    THREE_3_1(3001, "3串1"),
    THREE_3_4(3004, "3串4"),
    FOUR_4_1(4001, "4串1"),
    FOUR_4_11(40011, "4串11"),
    FIVE_5_1(5001, "5串1"),
    FIVE_5_26(50026, "5串26"),
    SIX_6_1(6001, "6串1"),
    SIX_6_57(60057, "6串57"),
    SEVEN_7_1(7001, "7串1"),
    SEVEN_7_120(700120, "7串120"),
    EIGHT_8_1(8001, "8串1"),
    EIGHT_8_247(800247, "8串247"),
    NINE_9_1(9001, "9串1"),
    NINE_9_502(900502, "9串502"),
    TEN_10_1(10001, "10串1"),
    TEN_10_1013(1001013, "10串1013");

    private final Integer code;
    private final String desc;
    DbPanDaSportSerialTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DbPanDaSportSerialTypeEnum fromCode(Integer code) {
        for (DbPanDaSportSerialTypeEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
