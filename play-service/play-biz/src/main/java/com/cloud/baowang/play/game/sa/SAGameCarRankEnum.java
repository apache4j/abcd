package com.cloud.baowang.play.game.sa;


import lombok.Getter;

/**
 * SA 牌的结果
 */
@Getter
public enum SAGameCarRankEnum {
    A(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    J(11, "J"),
    Q(12, "Q"),
    K(13, "K");

    private final Integer code;
    private final String description;

    SAGameCarRankEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    public static SAGameCarRankEnum byCode(Integer code) {
        for (SAGameCarRankEnum tmp : SAGameCarRankEnum.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

}
