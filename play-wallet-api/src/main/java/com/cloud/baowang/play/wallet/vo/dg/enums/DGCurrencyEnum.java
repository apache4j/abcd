package com.cloud.baowang.play.wallet.vo.dg.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DGCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    USD("USD","USD", "美元"),
    KVND("KVND","VND", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元");
    private final String code;
    private final String sexyCode;
    private final String symbol;

    DGCurrencyEnum(String code, String sexyCode, String symbol) {
        this.code = code;
        this.sexyCode = sexyCode;
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
            if (sexyCode.equals(type.getSexyCode())) {
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
        return currencyEnum.getSexyCode();
    }


    public static List<DGCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
