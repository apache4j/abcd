package com.cloud.baowang.play.game.evo.enums;


import lombok.Getter;


/**
 * SA 牌的花色
 */
@Getter
public enum EvoGameCarSuitEnum {
    SUIT_1("S", "♠"),
    SUIT_2("H", "♥"),
    SUIT_3("C", "♣"),
    SUIT_4("D", "♦");

    private final String code;
    private final String description;

    EvoGameCarSuitEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }


    public static EvoGameCarSuitEnum byCode(String code) {
        if(code == null){
            return null;
        }
        for (EvoGameCarSuitEnum tmp : EvoGameCarSuitEnum.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

}
