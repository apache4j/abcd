package com.cloud.baowang.play.game.dg2.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DGCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    USD("USD","USD", "美元"),
    USDT("USDT","USDT", "泰达币"),
    KVND("KVND","VND2", "越南盾K"),
    VND("VND","VND", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元");
    private String code;
    private String dg2Code;
    private String symbol;

    DGCurrencyEnum(String code, String dg2Code, String symbol) {
        this.code = code;
        this.dg2Code = dg2Code;
        this.symbol = symbol;
    }

    public static DGCurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        DGCurrencyEnum[] types = DGCurrencyEnum.values();
        for (DGCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static DGCurrencyEnum enumOfSexyCode(String sexyCode) {
        if (null == sexyCode) {
            return null;
        }
        DGCurrencyEnum[] types = DGCurrencyEnum.values();
        for (DGCurrencyEnum type : types) {
            if (sexyCode.equals(type.getDg2Code())) {
                return type;
            }
        }
        return null;
    }


    public static String getSexyCodeByCode(String code) {
        DGCurrencyEnum currencyEnum = enumOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getDg2Code();
    }


    public static List<DGCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
