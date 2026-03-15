package com.cloud.baowang.play.game.ace.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ACECurrencyEnum {


    BDT(CurrencyEnum.BDT.getCode(), "Bangladeshi Taka"),
    CNY(CurrencyEnum.CNY.getCode(), "Yuan Renminbi"),
    HKD(CurrencyEnum.HKD.getCode(), "Hong Kong Dollar"),
    IDR(CurrencyEnum.IDR.getCode(), "Indonesian Rupiah"),
    KHR(CurrencyEnum.KHR.getCode(), "Cambodian Riel"),
    MMK(CurrencyEnum.MMK.getCode(), "Burmese Kyat"),
    MYR(CurrencyEnum.MYR.getCode(), "Malaysian Ringgit"),
    SGD(CurrencyEnum.SGD.getCode(), "Singapore Dollar"),
    THB(CurrencyEnum.THB.getCode(), "Thai Baht"),
    USD(CurrencyEnum.USD.getCode(), "US Dollar"),
    //NOTE VND(1:1) == KVND
    VND(CurrencyEnum.VND.getCode(), "Vietnamese Dong"),
    AUD(CurrencyEnum.AUD.getCode(), "Australian Dollar"),

    //NOTE 未适配 USDT(CurrencyEnum.USDT.getCode(), "USDT"),


    UNKNOWN(CurrencyEnum.BDT.getCode(), "Unknown Currency Error."),

    ;

    private final String code;
    private final String desc;

    public static ACECurrencyEnum fromCode(String code) {
        for (ACECurrencyEnum value : ACECurrencyEnum.values()) {
            if (value.code.equals(code) ) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
