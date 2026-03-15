package com.cloud.baowang.play.game.dg2.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum DG2ResultTypeEnum {

    Crab("Crab", "蟹", "Crab"), //
    Fish("Fish", "鱼", "Fish"), //
    Gourd("Gourd", "葫芦", "Gourd"), //
    Prawn("Prawn", "虾", "Prawn"), //
    Chicken("Chicken", "公鸡", "Chicken"), //
    Tiger("Tiger", "老虎", "Tiger"),
    SEDIE_4W("0", "4白", "4 White"),
    SEDIE_4R("4", "4红", "4 Red"),
    SEDIE_3W1R("1", "3白1红", "3 White 1 Red"),
    SEDIE_2W2R("2", "2白2红", "2 White 2 Red"),
    SEDIE_3R1W("3", "3红1白", "3 Red 1 White"),

    ;
    private String code;
    private String zh_CN;
    private String en_US;

    DG2ResultTypeEnum(String code, String zh_CN, String en_US) {
        this.code = code;
        this.zh_CN = zh_CN;
        this.en_US = en_US;
    }

    public static Optional<DG2ResultTypeEnum> fromCode(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }


}
