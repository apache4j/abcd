package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum TFPlayTypeEnum {
    WIN("WIN", "主盘口独赢"),
    _1X2("1X2", "独赢"),
    AH("AH", "让分局"),
    OU("OU", "大小"),
    OE("OE", "单双"),
    SPWINMAP("SPWINMAP", "局独赢"),
    WINMAP("WINMAP", "局独赢比分"),
    SPHA("SPHA", "特别主客"),
    SPYN("SPYN", "特别是否"),
    SPOE("SPOE", "特别单双"),
    SPOU("SPOU", "特别大小"),
    SP1X2("SP1X2", "特别1X2"),
    OR("OR", "冠军盘"),
    SPOR("SPOR", "特别多项"),
    SPXX("SPXX", "特别双项"),
    SPMOR("SPMOR", "Special Proposition Multi Outright"),
    SPOEU("SPOEU", "Special Proposition Over Equal Under"),
    SPMM("SPMM", "Special Proposition Min Max"),
    SPRLE("SPRLE", "Special Proposition Range Less Than Equal"),
    SP777("SP777", "Special Proposition 777"),
    SPAD("SPAD", "Special Proposition Attack Defend");

    private final String code;
    private final String name;

    TFPlayTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TFPlayTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        TFPlayTypeEnum[] types = TFPlayTypeEnum.values();
        for (TFPlayTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
