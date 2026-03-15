package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportJumpSupportEnum {

    TY("ty", "从体育跳转"),
    ZR("zr", "从真人跳转"),
    QP("qp", "从棋牌跳转"),
    DY("dy", "从电游（捕⻥）跳转"),
    LHJ("lhj", "从电游（老虎机）跳转"),
    CP("cp", "从彩票跳转"),
    DJ("dj", "从电竞跳转");

    private final String code;
    private final String desc;

    DbPanDaSportJumpSupportEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
