package com.cloud.baowang.play.game.db.acelt.enums;


import java.util.Objects;

public enum ACELTCurrencyEnum {

    CNY(1, "CNY", "人民币"),
    USD(2, "USD", "美元"),
    SGD(3, "SGD", "新加坡元"),
    MYR(4, "MYR", "马来西亚林吉特"),
    TWD(5, "TWD", "新台币"),
    THB(6, "THB", "泰铢"),
    BND(7, "BND", "文莱林吉特"),
    VND(8, "VND", "越南盾"),
    JPY(9, "JPY", "日元"),
    KRW(10, "KRW", "韩元"),
    MMK(11, "MMK", "缅甸元"),
    BRL(12, "BRL", "巴西雷亚尔"),
    COP(13, "COP", "哥伦比亚比索"),
    USDT(14, "USDT", "USDT"),
    IDR(15, "IDR", "印尼盾"),
    VNDK(16, "KVND", "KVND"),
    INR(17, "INR", "印度卢比"),
    ALL(888, "ALL", "全部（统计报表用）"),
    ALL_CNY(999, "ALL_CNY", "全部（人民币，统计报表用）"),
    UNKNOWN(-1, "", "");;


    private final int code;
    private final String currency;
    private final String desc;

    ACELTCurrencyEnum(int code, String currency, String desc) {
        this.code = code;
        this.currency = currency;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDesc() {
        return desc;
    }

    public static ACELTCurrencyEnum fromCode(String currency) {
        for (ACELTCurrencyEnum e : values()) {
            if (Objects.equals(e.currency, currency)) {
                return e;
            }
        }
        return UNKNOWN;
    }

}
