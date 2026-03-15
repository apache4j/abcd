package com.cloud.baowang.play.game.fc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FCGameTypeEnum {



    fishing("1","捕鱼机", "21"),
    arcade("2","老虎机", "22"),
    slot("7","街机", "27"),
    table("8","桌面游戏", "28"),


    unknown("404","未知游戏类型", "404"),

    ;

    private final String code;
    private final String desc;
    private final String encode;

    public static FCGameTypeEnum fromCode(String code) {
        for (FCGameTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return unknown;
    }
}
