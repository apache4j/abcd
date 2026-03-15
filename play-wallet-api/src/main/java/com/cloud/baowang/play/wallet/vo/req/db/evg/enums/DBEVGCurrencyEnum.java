package com.cloud.baowang.play.wallet.vo.req.db.evg.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum DBEVGCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","CNY", "人民币"),
    USD("USD","USD", "美元"),
    KVND("KVND","VNDK", "越南盾K"),
    MYR("MYR","MYR", "马来西亚林吉特"),
    PHP("PHP","PHP", "菲律宾比索"),
    PKR("PKR","PKR", "巴基斯坦卢比"),
    INR("INR","INR", "印度卢比"),
    KRW("KRW","KRW", "韩元"),
    VND("VND","VND", "越南盾"),
    BRL("BRL","BRL", "巴西雷亚尔"),
    USDT("USDT","USDT", "泰达币");
    private final String code;
    private final String dbCode;
    private final String symbol;

    DBEVGCurrencyEnum(String code, String dbCode, String symbol) {
        this.code = code;
        this.dbCode = dbCode;
        this.symbol = symbol;
    }

    public static DBEVGCurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        DBEVGCurrencyEnum[] types = DBEVGCurrencyEnum.values();
        for (DBEVGCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static DBEVGCurrencyEnum enumOfJdbCode(String jdbCode) {
        if (null == jdbCode) {
            return null;
        }
        DBEVGCurrencyEnum[] types = DBEVGCurrencyEnum.values();
        for (DBEVGCurrencyEnum type : types) {
            if (jdbCode.equals(type.getDbCode())) {
                return type;
            }
        }
        return null;
    }


    public static String getJdbCodeByCode(String code) {
        DBEVGCurrencyEnum currencyEnum = enumOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getDbCode();
    }


    public static List<DBEVGCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
