package com.cloud.baowang.play.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum GameHotRemTypeEnum {
    HOT_REM_TYPE("hot_rem_type", "热门推荐"),
    FRONT_PAGE("front_page", "首页");

    private static final long aLong = 0L;
    private final String code;
    private final String name;

    public static GameHotRemTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        GameHotRemTypeEnum[] types = GameHotRemTypeEnum.values();
        for (GameHotRemTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }



}
