package com.cloud.baowang.play.game.jdb.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum JDBCurrencyEnum {

    //CNY/USDT/USD/VNDK(1:1)/MYR/PHP/PKR/INR/KRW

    CNY("CNY","RB", "人民币"),
    USD("USD","US", "美元"),
    KVND("KVND","VN", "越南盾K"),
    MYR("MYR","RM", "马来西亚林吉特"),
    PHP("PHP","PP", "菲律宾比索"),
    PKR("PKR","PK", "巴基斯坦卢比"),
    INR("INR","RS", "印度卢比"),
    KRW("KRW","KW", "韩元"),
    VND("VND","VNO", "越南盾"),
    USDT("USDT","UST", "USDT");
    private String code;
    private String jdbCode;
    private String symbol;

    JDBCurrencyEnum(String code, String jdbCode, String symbol) {
        this.code = code;
        this.jdbCode = jdbCode;
        this.symbol = symbol;
    }

    public static JDBCurrencyEnum enumOfCode(String code) {
        if (null == code) {
            return null;
        }
        JDBCurrencyEnum[] types = JDBCurrencyEnum.values();
        for (JDBCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static JDBCurrencyEnum enumOfJdbCode(String jdbCode) {
        if (null == jdbCode) {
            return null;
        }
        JDBCurrencyEnum[] types = JDBCurrencyEnum.values();
        for (JDBCurrencyEnum type : types) {
            if (jdbCode.equals(type.getJdbCode())) {
                return type;
            }
        }
        return null;
    }


    public static String getJdbCodeByCode(String code) {
        JDBCurrencyEnum currencyEnum = enumOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getJdbCode();
    }


    public static List<JDBCurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
