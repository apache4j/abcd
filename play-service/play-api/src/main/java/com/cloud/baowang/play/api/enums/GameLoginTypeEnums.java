package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum GameLoginTypeEnums {

    URL("url", "URL链接"),
    HTML("html", "HTML内容"),
    TOKEN("token", "token"),
    ;

    private final String type;
    private final String description;

    GameLoginTypeEnums(String type, String description) {
        this.type = type;
        this.description = description;
    }

}
