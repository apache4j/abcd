package com.cloud.baowang.play.game.sa;

import lombok.Getter;

@Getter
public enum SaPokdengPointEnum {

    POINT_0(1, "0"),
    POINT_1(2, "1"),
    POINT_2(3, "2"),
    POINT_3(4, "3"),
    POINT_4(5, "4"),
    POINT_5(6, "5"),
    POINT_6(7, "6"),
    POINT_7(8, "7"),
    POINT_71(9, "7.1"),
    POINT_72(10, "7.2"),
    POINT_73(11, "7.3"),
    POINT_74(12, "7.4"),
    POINT_75(13, "7.5"),
    POINT_8(14, "8"),
    POINT_9(15, "9");

    private final Integer code;
    private final String desc;

    SaPokdengPointEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SaPokdengPointEnum fromCode(int code) {
        for (SaPokdengPointEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

}
