package com.cloud.baowang.play.api.enums.venue;

import lombok.Getter;

@Getter
public enum VenueJoinTypeEnum {

    DATA_SOURCE(1, "数据源"),
    VENUE(2, "场馆"),
    GAME(3, "游戏");

    private final Integer code;
    private final String description;

    VenueJoinTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
