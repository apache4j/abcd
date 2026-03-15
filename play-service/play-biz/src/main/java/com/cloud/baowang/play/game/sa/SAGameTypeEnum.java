package com.cloud.baowang.play.game.sa;


import lombok.Getter;

@Getter
public enum SAGameTypeEnum {

    BAC("bac", "百家乐"),
    DTX("dtx", "龙虎"),
    SICBO("sicbo", "骰宝"),
    ROT("rot", "轮盘"),
    POKDENG("pokdeng", "博丁"),
    ANDARBAHAR("andarbahar", "安达巴哈"),
    BLACKJACK("blackjack", "黑杰克"),
    XOCDIA("xocdia", "色碟"),
    THAIHILO("thaihilo", "泰国骰宝"),
    FISHPRAWNCRAB("fishprawncrab", "鱼虾蟹"),
    ULTRAROULETTE("ultraroulette", "至尊轮盘"),
    TEENPATTI2020("teenpatti2020", "印度炸金花");

    private final String code;
    private final String description;

    SAGameTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
