package com.cloud.baowang.play.game.dg2.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum DG2SedieEnum {

    //色碟
    SedieZERO("zero", "4白", "zero","0"),
    SedieONE("one", "3白1红", "one","1"),
    SedieTWO("two", "2红2白", "two","2"),
    SedieTHREE("three", "3红1白", "three","3"),
    SedieFOUR("four", "4红", "four","4"),
    SedieODD("odd", "单", "odd","5"),
    SedieEVEN("even", "双", "even","6"),
    SedieBIG("big", "大", "big","7"),
    SedieSMALL("small", "小", "small","8"),
    SedieBIGH("bigH", "高赔大", "bigH","9"),
    SedieSMALLH("smallH", "高赔小", "smallH","10"),

    SedieZERO_FOUR("zeroFour", "4红或4白", "zeroFour","11");




    private final String code;
    private final String desc;
    private final String shCode;
    private final String result;

    DG2SedieEnum(String code, String desc, String shCode,String result) {
        this.code = code;
        this.desc = desc;
        this.shCode = shCode;
        this.result = result;
    }

    public static Optional<DG2SedieEnum> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }

    public static DG2SedieEnum getEnum(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
    }

    public static DG2SedieEnum getResultEnum(String result) {
        return Arrays.stream(values())
                .filter(e -> e.result.equalsIgnoreCase(result))
                .findFirst()
                .orElse(null);
    }

}
