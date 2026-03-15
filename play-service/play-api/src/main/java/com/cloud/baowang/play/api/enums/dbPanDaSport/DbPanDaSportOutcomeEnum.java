package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportOutcomeEnum {
    PUSH(2, "走水"),
    LOSE(3, "输"),
    WIN(4, "赢"),
    WIN_HALF(5, "赢半"),
    LOSE_HALF(6, "输半"),
    CANCELLED(7, "赛事取消"),
    POSTPONED(8, "赛事延期");

    private final int code;
    private final String desc;

    DbPanDaSportOutcomeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
