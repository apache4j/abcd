package com.cloud.baowang.play.game.sa;


import lombok.Getter;


/**
 * SA 牌的花色
 */
@Getter
public enum SAGameCarSuitEnum {
    SUIT_1(1, "♠","♠"),
    SUIT_2(2, "♥","♥"),
    SUIT_3(3, "♣","♣"),
    SUIT_4(4, "♦","♦");

    private final Integer code;
    private final String description;
    private final String icon;

    SAGameCarSuitEnum(Integer code, String description,String icon) {
        this.code = code;
        this.description = description;
        this.icon = icon;
    }


    public static SAGameCarSuitEnum byCode(Integer code) {
        if(code == null){
            return null;
        }
        for (SAGameCarSuitEnum tmp : SAGameCarSuitEnum.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

}
