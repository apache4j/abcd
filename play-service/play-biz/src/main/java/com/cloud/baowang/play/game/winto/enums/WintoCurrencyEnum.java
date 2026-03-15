package com.cloud.baowang.play.game.winto.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum WintoCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","CNY", "人民币"),
    USD("USD","USD", "美元"),
    USDT("USDT","USDT", "泰达币"),
    KVND("KVND","VND", "越南盾K"),
    VND("VND","PTV", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元");
    private String code;
    private String thirdCode;
    private String symbol;

    WintoCurrencyEnum(String code, String thirdCode, String symbol) {
        this.code = code;
        this.thirdCode = thirdCode;
        this.symbol = symbol;
    }

    public static WintoCurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        WintoCurrencyEnum[] types = WintoCurrencyEnum.values();
        for (WintoCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }




    public static String getThirdCodeByCode(String code) {
        WintoCurrencyEnum currencyEnum = enumOfCode(code);
        if (currencyEnum == null) {
            return null;
        }
        return currencyEnum.getThirdCode();
    }


    public static List<WintoCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
