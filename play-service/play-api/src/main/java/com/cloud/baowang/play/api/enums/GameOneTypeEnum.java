package com.cloud.baowang.play.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum GameOneTypeEnum {
    MULTI_GAME(1, "多游戏"),
    VENUE(2, "场馆"),
    LOTTERY_ORIGINAL_SOUND(3, "彩票原声"),
    SBA_ORIGINAL_SOUND(4, "体育原声");

    private final Integer code;
    private final String name;

    public static GameOneTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        GameOneTypeEnum[] types = GameOneTypeEnum.values();
        for (GameOneTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }



    public static List<GameOneTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
