package com.cloud.baowang.play.api.enums.sb;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SBPankouType {

    CHAMPION(1, "冠军"),
    EVENT(2, "赛事"),
    EVENT_SPORT_ID(3, "赛事-球类"),
    CHAMPION_SPORT_ID(4, "冠军-球类");

    private final Integer code;
    private final String name;


}
