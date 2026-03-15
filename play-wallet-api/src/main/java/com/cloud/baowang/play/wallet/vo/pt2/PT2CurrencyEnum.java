package com.cloud.baowang.play.wallet.vo.pt2;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PT2CurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","CNY", "人民币"),;
    private final String code;
    private final String venueCurrency;
    private final String symbol;

    PT2CurrencyEnum(String code, String venueCurrency, String symbol) {
        this.code = code;
        this.venueCurrency = venueCurrency;
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
            if (sexyCode.equals(type.getVenueCurrency())) {
                return type;
            }
        }
        return null;
    }


    public static String getSexyCodeByCode(String code) {
        PT2CurrencyEnum currencyEnum = enumOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getVenueCurrency();
    }


    public static List<PT2CurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
