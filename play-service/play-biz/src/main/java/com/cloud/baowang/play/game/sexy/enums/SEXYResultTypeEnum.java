package com.cloud.baowang.play.game.sexy.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SEXYResultTypeEnum {

    Crab("Crab", "蟹", "Crab"), //
    Fish("Fish", "鱼", "Fish"), //
    Gourd("Gourd", "葫芦", "Gourd"), //
    Prawn("Prawn", "虾", "Prawn"), //
    Chicken("Chicken", "公鸡", "Chicken"), //
    Tiger("Tiger", "老虎", "Tiger"),
    SEDIE_4W("4W", "4白", "4 White"),
    SEDIE_4R("4R", "4红", "4 Red"),
    SEDIE_3W1R("3W1R", "3白1红", "3 White 1 Red"),
    SEDIE_2W2R("2W2R", "2白2红", "2 White 2 Red"),
    SEDIE_3R1W("3R1W", "3红1白", "3 Red 1 White"),

    ;
    private String code;
    private String zh_CN;
    private String en_US;

    SEXYResultTypeEnum(String code, String zh_CN, String en_US) {
        this.code = code;
        this.zh_CN = zh_CN;
        this.en_US = en_US;
    }

    public static Optional<SEXYResultTypeEnum> fromCode(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }


}
