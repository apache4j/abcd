package com.cloud.baowang.play.game.dg2.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SBPlayTypeEnum {

    //骰宝
    SB_BIG("big", "大", "big"),
    SB_SMALL("small", "小", "small"),
    SB_ODD("odd", "单", "odd"),
    SB_EVEN("even", "双", "even"),
    SB_ALL_DICES("allDices", "全围", "allDices"),
    SB_THREE_FORCES("threeForces", "三军", "threeForces"),
    SB_NINE_WAY_GARDS("nineWayGards", "短牌", "nineWayGards"),
    SB_PAIRS("pairs", "长牌", "pairs"),
    SB_SURROUND_DICES("surroundDices", "围骰", "surroundDices"),
    SB_POINTS("points", "点数", "points");




    private final String code;
    private final String desc;
    private final String shCode;

    SBPlayTypeEnum(String code, String desc, String shCode) {
        this.code = code;
        this.desc = desc;
        this.shCode = shCode;
    }

    // 根据属性名查找枚举
    public static Optional<SBPlayTypeEnum> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }

    public static SBPlayTypeEnum getEnum(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
    }
}
