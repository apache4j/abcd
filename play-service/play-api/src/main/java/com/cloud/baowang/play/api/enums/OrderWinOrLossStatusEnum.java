package com.cloud.baowang.play.api.enums;


import lombok.Getter;

@Getter
public enum OrderWinOrLossStatusEnum {

    WIN(1, "赢"),
    DRAW(0, "和"),
    LOSE(-1, "输");

    private final Integer code;
    private final String desc;

    OrderWinOrLossStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
