package com.cloud.baowang.play.api.enums.acelt;

import com.cloud.baowang.common.core.enums.CurrencyEnum;

public enum ACELTCurrencyEnum {
    USD("USD", CurrencyEnum.USD.getCode(), "美元", "1:1"),
    USDT("USDT", CurrencyEnum.USDT.getCode(), "美元", "1:1"),
    CNY("CNY", CurrencyEnum.CNY.getCode(), "人民币", "1:1"),
    BRL("BRL", CurrencyEnum.BRL.getCode(), "巴西雷亚尔", "1:1"),
    MYR("MYR", CurrencyEnum.MYR.getCode(), "马元", "1:1"),
    VND("VND", CurrencyEnum.VND.getCode(), "越南盾", "1:1000"),
    KVND("KVND", CurrencyEnum.KVND.getCode(), "越南盾", "1:1000"),
    PHP("PHP", CurrencyEnum.PHP.getCode(), "菲律宾比索", "1:1"),
    PKR("PKR", CurrencyEnum.PKR.getCode(), "巴基斯坦卢比", "1:1"),
    INR("INR", CurrencyEnum.INR.getCode(), "印度卢比","1:1"),
    KRW("KRW", CurrencyEnum.KRW.getCode(), "韩元","1:1");


    private final String currencyCode;
    private final String platformCurrencyCode;
    private final String name;
    private final String exchangeRate;


    ACELTCurrencyEnum(String currencyCode, String platformCurrencyCode, String name, String exchangeRate) {
        this.currencyCode = currencyCode;
        this.platformCurrencyCode = platformCurrencyCode;
        this.name = name;
        this.exchangeRate = exchangeRate;
    }

    public String getPlatformCurrencyCode() {
        return platformCurrencyCode;
    }


    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getName() {
        return name;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public static ACELTCurrencyEnum getPlatCurrencyEnum(String platCurrencyCode) {
        if (null == platCurrencyCode) {
            return null;
        }
        ACELTCurrencyEnum[] types = ACELTCurrencyEnum.values();
        for (ACELTCurrencyEnum type : types) {
            if (platCurrencyCode.equals(type.getPlatformCurrencyCode())) {
                return type;
            }
        }
        return null;
    }

    public static ACELTCurrencyEnum getCurrencyEnum(String currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        ACELTCurrencyEnum[] types = ACELTCurrencyEnum.values();
        for (ACELTCurrencyEnum type : types) {
            if (currencyCode.equals(type.getCurrencyCode())) {
                return type;
            }
        }
        return null;
    }


}
