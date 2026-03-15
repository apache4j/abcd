package com.cloud.baowang.play.game.pp.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPCurrencyEnum {

    USD(CurrencyEnum.USD.getCode(),"United States Dollar"),
    USDT(CurrencyEnum.USDT.getCode(),"USDT"),

    PKR(CurrencyEnum.PKR.getCode(),"PKR"),
    MYR(CurrencyEnum.MYR.getCode(),"Malaysian Ringgit"),
    PHP(CurrencyEnum.PHP.getCode(),"Philippine Peso"),
    HUF(CurrencyEnum.HUF.getCode(),"Hungarian Forint"),

    INR(CurrencyEnum.INR.getCode(),"Indian Rupee"),

    VND(CurrencyEnum.VND.getCode(),"Vietnamese Dong"),
    VND2(CurrencyEnum.KVND.getCode(),"Vietnamese Dong"),
    KRW(CurrencyEnum.KRW.getCode(),"South Korean Won"),
    MOP(CurrencyEnum.MOP.getCode(),"MOP"),
    ISK(CurrencyEnum.ISK.getCode(),"ISK"),
    HKD(CurrencyEnum.HKD.getCode(),"Hong Kong Dollar"),
    ITL(CurrencyEnum.HKD.getCode(),"ITL"),
    BRL(CurrencyEnum.BRL.getCode(),"Brazilian Real"),
    ESP(CurrencyEnum.ESP.getCode(),"西班牙"),
    DEM(CurrencyEnum.DEM.getCode(),"DEM"),
    //NOTE 荷兰币.
    NLG(CurrencyEnum.NLG.getCode(),"NLG"),



    EUR(CurrencyEnum.EUR.getCode(),"Euro"),
    JPY(CurrencyEnum.JPY.getCode(),"Japanese Yen"),
    GBP(CurrencyEnum.GBP.getCode(),"British Pound"),
    CNY(CurrencyEnum.CNY.getCode(),"Chinese Yuan"),
    CHF(CurrencyEnum.CHF.getCode(),"Swiss Franc"),
    CAD(CurrencyEnum.CAD.getCode(),"Canadian Dollar"),
    AUD(CurrencyEnum.AUD.getCode(),"Australian Dollar"),
    SGD(CurrencyEnum.SGD.getCode(),"Singapore Dollar"),
    SEK(CurrencyEnum.SEK.getCode(),"Swedish Krona"),
    NOK(CurrencyEnum.NOK.getCode(),"Norwegian Krone"),
    DKK(CurrencyEnum.DKK.getCode(),"Danish Krone"),
    MXN(CurrencyEnum.MXN.getCode(),"Mexican Peso"),
    RUB(CurrencyEnum.RUB.getCode(),"Russian Ruble"),
    TRY(CurrencyEnum.TRY.getCode(),"Turkish Lira"),
    ZAR(CurrencyEnum.ZAR.getCode(),"South African Rand"),
    THB(CurrencyEnum.THB.getCode(),"Thai Baht"),
    TWD(CurrencyEnum.TWD.getCode(),"New Taiwan Dollar"),
    IDR(CurrencyEnum.IDR.getCode(),"Indonesian Rupiah"),
    PLN(CurrencyEnum.PLN.getCode(),"Polish Zloty"),

    CZK(CurrencyEnum.CZK.getCode(),"Czech Koruna"),
    ILS(CurrencyEnum.ILS.getCode(),"Israeli New Shekel"),
    AED(CurrencyEnum.AED.getCode(),"United Arab Emirates Dirham"),
    SAR(CurrencyEnum.SAR.getCode(),"Saudi Riyal"),



    COP(CurrencyEnum.COP.getCode(),"Colombian Peso"),
    ARS(CurrencyEnum.ARS.getCode(),"Argentine Peso"),
    UAH(CurrencyEnum.UAH.getCode(),"Ukrainian Hryvnia"),
    NGN(CurrencyEnum.NGN.getCode(),"Nigerian Naira"),
    EGP(CurrencyEnum.EGP.getCode(),"Egyptian Pound"),
    RON(CurrencyEnum.RON.getCode(),"Romanian Leu"),
    KZT(CurrencyEnum.KZT.getCode(),"Kazakhstani Tenge"),
    CLP(CurrencyEnum.CLP.getCode(),"Chilean Peso"),

    UNKNOWN("UNKNOWN","UNKNOWN currency");

    private final String code;
    private final String fullName;


    public static PPCurrencyEnum getByCode(String code) {

        for (PPCurrencyEnum ppCurrencyEnum : PPCurrencyEnum.values()) {
            if (ppCurrencyEnum.code.equals(code)) {
                return ppCurrencyEnum;
            }
        }
        return UNKNOWN; // 异常
    }
}
