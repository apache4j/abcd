package com.cloud.baowang.play.api.vo.card;

import com.alibaba.fastjson2.annotation.JSONCreator;

/**
 * 每张牌的颜色
 */
public enum PokerSuit {
    SPADE(1, "黑桃"),
    HEART(2, "红桃"),
    CLUB(3, "梅花"),
    DIAMOND(4, "方块"),
    ALL(5, "大小王");

    private final int code;
    private final String desc;

    PokerSuit(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }



    @JSONCreator
    public static PokerSuit fromString(String name) {
        return PokerSuit.valueOf(name);
    }
    public static PokerSuit fromCode(int code) {
        for (PokerSuit suit : values()) {
            if (suit.code == code) {
                return suit;
            }
        }
        throw new IllegalArgumentException("无效的花色 code: " + code);
    }

    public static PokerSuit fromLetter(String letter) {
        switch (letter.toUpperCase()) {
            case "S": return SPADE;
            case "H": return HEART;
            case "C": return CLUB;
            case "D": return DIAMOND;
            default: return ALL;
        }
    }
//    public static void main(String[] args) {
//        System.out.println(PokerSuit.ALL.compareTo(PokerSuit.DIAMOND));
//    }

}