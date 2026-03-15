package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum GameInfoLabelEnum {
    NOT(0,"无"),
    HOT_RECOMMENDED(1, "热门推荐"),
    NEWS(2, "新游戏"),
    FAVORITE(3, "收藏");
    ;
    private Integer code;
    private String name;

    GameInfoLabelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static GameInfoLabelEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        GameInfoLabelEnum[] types = GameInfoLabelEnum.values();
        for (GameInfoLabelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        GameInfoLabelEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<GameInfoLabelEnum> getList() {
        return Arrays.asList(values());
    }

}
