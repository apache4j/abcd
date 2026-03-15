package com.cloud.baowang.play.game.playtech.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PT2CurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","CNY", "人民币"),
    USD("USD","USD", "美元"),
    KVND("KVND","VND", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元");
    private String code;
    private String pt2Code;
    private String symbol;

    PT2CurrencyEnum(String code, String pt2Code, String symbol) {
        this.code = code;
        this.pt2Code = pt2Code;
        this.symbol = symbol;
    }

    public static PT2CurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        PT2CurrencyEnum[] types = PT2CurrencyEnum.values();
        for (PT2CurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static PT2CurrencyEnum enumOfSexyCode(String sexyCode) {
        if (null == sexyCode) {
            return null;
        }
        PT2CurrencyEnum[] types = PT2CurrencyEnum.values();
        for (PT2CurrencyEnum type : types) {
            if (sexyCode.equals(type.getPt2Code())) {
                return type;
            }
        }
        return null;
    }


    public static String getPt2CodeByCode(String code) {
        PT2CurrencyEnum currencyEnum = enumOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getPt2Code();
    }


    public static List<PT2CurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
