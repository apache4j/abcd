package com.cloud.baowang.play.api.enums.dbDj;

import lombok.Getter;

/**
 * 串关类型（Parley Type）
 * 1：普通注单
 * 2：2串1
 * 3：3串1
 * 4：4串1
 * 5：5串1
 * 6：6串1
 * 7：7串1
 * 8：8串1
 */
@Getter
public enum DbDjParleyTypeEnum {

    SINGLE(1, "普通注单"),
    TWO_2_1(2, "2串1"),
    THREE_3_1(3, "3串1"),
    FOUR_4_1(4, "4串1"),
    FIVE_5_1(5, "5串1"),
    SIX_6_1(6, "6串1"),
    SEVEN_7_1(7, "7串1"),
    EIGHT_8_1(8, "8串1");

    private final int code;
    private final String name;

    DbDjParleyTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DbDjParleyTypeEnum fromCode(int code) {
        for (DbDjParleyTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
