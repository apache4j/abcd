package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum GameTwoModelEnum {
    GAME("GAME", "游戏"),
    VENUE("VENUE", "平台");

    private String code;
    private String name;

    GameTwoModelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static GameTwoModelEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        GameTwoModelEnum[] types = GameTwoModelEnum.values();
        for (GameTwoModelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(String code){
        GameTwoModelEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<GameTwoModelEnum> getList() {
        return Arrays.asList(values());
    }

}
