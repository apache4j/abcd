package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum LobbyGameLabelEnum {
    GAME("GAME", "游戏"),
    VENUE("VENUE", "平台");

    private final String code;
    private final String name;

    LobbyGameLabelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
