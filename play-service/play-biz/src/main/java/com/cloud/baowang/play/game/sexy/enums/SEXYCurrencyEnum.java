package com.cloud.baowang.play.game.sexy.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SEXYCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","CNY", "人民币"),
    USD("USD","USD", "美元"),
    KVND("KVND","VND", "越南盾K"),
    VND("VND","PTV", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元");
    private String code;
    private String sexyCode;
    private String symbol;

    SEXYCurrencyEnum(String code, String sexyCode, String symbol) {
        this.code = code;
        this.sexyCode = sexyCode;
        this.symbol = symbol;
    }

    public static SEXYCurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        SEXYCurrencyEnum[] types = SEXYCurrencyEnum.values();
        for (SEXYCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static SEXYCurrencyEnum enumOfSexyCode(String sexyCode) {
        if (null == sexyCode) {
            return null;
        }
        SEXYCurrencyEnum[] types = SEXYCurrencyEnum.values();
        for (SEXYCurrencyEnum type : types) {
            if (sexyCode.equals(type.getSexyCode())) {
                return type;
            }
        }
        return null;
    }


    public static String getSexyCodeByCode(String code) {
        SEXYCurrencyEnum currencyEnum = enumOfCode(code);
        if (currencyEnum == null) {
            return null;
        }
        return currencyEnum.getSexyCode();
    }


    public static List<SEXYCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
