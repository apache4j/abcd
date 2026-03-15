package com.cloud.baowang.play.game.s128.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BetContentEnum {

    WALA("凤"),
    BDD("合"),
    MERON("龙"),
    FTD("大和"),
    VOID("作废"),
            ;

    final String nameCn;

    public static String fromName(String name) {
        if (name == null) {
            return "";
        }
        try {
            return BetContentEnum.valueOf(name).getNameCn();
        } catch (IllegalArgumentException e) {
            return ""; // 或者抛出业务异常，看你需要
        }
    }
}
