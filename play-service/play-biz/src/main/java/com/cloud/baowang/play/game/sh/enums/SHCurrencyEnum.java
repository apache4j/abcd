package com.cloud.baowang.play.game.sh.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.Getter;

@Getter
public enum SHCurrencyEnum {

    USD("USD", "美元", CurrencyEnum.USD.getCode()),
    USDT("USDT", "美元", CurrencyEnum.USDT.getCode()),
    CNY("CNY", "人民币", CurrencyEnum.CNY.getCode()),
    BRL("BRL", "巴西布雷尔", CurrencyEnum.BRL.getCode()),
    TWD("TWD", "新台币", CurrencyEnum.TWD.getCode()),
    INR("INR", "卢比", CurrencyEnum.INR.getCode()),
    MYR("MYR", "令吉(马来西亚)", CurrencyEnum.MYR.getCode()),
    THB("THB", "泰铢", CurrencyEnum.THB.getCode()),
    SGD("SGD", "新加坡元", CurrencyEnum.SGD.getCode()),
    VND("VND", "越南盾", CurrencyEnum.VND.getCode()),
    VND_K("VND(K)", "越南盾(K)", CurrencyEnum.KVND.getCode()),
    HKD("HKD", "港币", CurrencyEnum.HKD.getCode()),
    KHR("KHR", "瑞尔(柬埔寨)", CurrencyEnum.KHR.getCode()),
    KHR_K("KHR(K)", "瑞尔(K)", CurrencyEnum.KHR.getCode()),
    IDR("IDR", "印尼盾", CurrencyEnum.IDR.getCode()),
    IDR_K("IDR(K)", "印尼盾(K)", CurrencyEnum.IDR.getCode()),
    PHP("PHP", "披索", CurrencyEnum.PHP.getCode()),
    BDT("BDT", "孟加拉塔卡", CurrencyEnum.BDT.getCode()),
    PKR("PKR", "巴基斯坦卢比", CurrencyEnum.PKR.getCode()),
    KRW("KRW", "韩币", CurrencyEnum.KRW.getCode());


    private final String code;
    private final String name;
    private final String platCurrencyCode;

    SHCurrencyEnum(String code, String name, String platCurrencyCode) {
        this.code = code;
        this.name = name;
        this.platCurrencyCode = platCurrencyCode;
    }

    public static SHCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (SHCurrencyEnum tmp : SHCurrencyEnum.values()) {
            if (tmp.getPlatCurrencyCode().equals(platCurrencyCode)) {
                return tmp;
            }
        }
        return null;
    }


}
