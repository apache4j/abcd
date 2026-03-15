package com.cloud.baowang.play.api.enums.dbDj;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjMatchTypeEnum {

    NORMAL(1, "正常"),
    CHAMPION(2, "冠军"),
    BATTLE_ROYALE(3, "大逃杀"),
    BASKETBALL(4, "篮球"),
    ANCHOR(5, "主播盘"),
    FOOTBALL(6, "足球");

    private final Integer code;
    private final String name;

    DbDjMatchTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    // 根据 code 获取枚举
    public static DbDjMatchTypeEnum fromCode(Integer code) {
        for (DbDjMatchTypeEnum e : values()) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }


}
