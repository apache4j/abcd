package com.cloud.baowang.play.api.enums.dg2;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DG2GameTypeEnum {

    BAC("1", "百家乐","2"),
    BAC_2("2", "保险百家乐","2"),
    BAC_8("8", "保险百家乐","2"),
    BBAC("41", "区块链百家乐","2"),

    DTX("3", "龙虎","4"),
    BDTX("42", "区块链龙虎","4"),

    ROT("4", "轮盘","15"),
    BROT("47", "区块链轮盘","15"),

    SBR("5", "骰宝","17"),
    BSBR("48", "区块链骰宝","17"),

    FT("6", "番摊","16"),

    XOCDIA("14", "色碟","14"),


    ZJH("11", "炸金花","12"),
    BZJH("43", "区块链炸金花","12"),


    SG("16", "三公","5"),
    BSG("45", "区块链三公","5"),

    ANDARBAHAR("20", "安达巴哈","19"),
    BANDARBAHAR("46", "区块链安达巴哈","19"),


    COW("7", "斗牛","3"),
    BCOW("44", "区块链炸牛牛","3"),


    BJ("21", "21点","18"),
    BBJ("53", "区块链12点","18"),

    UNKNOWN("0", "","");

    private final String code;
    private final String description;
    private final String gameType;

    DG2GameTypeEnum(String code, String description, String gameType) {
        this.code = code;
        this.description = description;
        this.gameType = gameType;
    }

    public static DG2GameTypeEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        DG2GameTypeEnum[] types = DG2GameTypeEnum.values();
        for (DG2GameTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public static DG2GameTypeEnum fromGameType(String gameType) {
        return Arrays.stream(values())
                .filter(e -> e.gameType.equalsIgnoreCase(gameType))
                .findFirst().orElse(UNKNOWN);
    }
}
