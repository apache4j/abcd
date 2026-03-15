package com.cloud.baowang.play.game.cmd.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CMDCurrencyEnums {
//    AED(CurrencyEnum.AED.getCode(),"阿联酋迪拉姆 1:1"),
    MYR(CurrencyEnum.MYR.getCode(),"MYR","马币"),
    USD(CurrencyEnum.USD.getCode(),"USD","美元"),
    CNY(CurrencyEnum.CNY.getCode(),"CNY","人民币"),
    VND(CurrencyEnum.KVND.getCode(), "VD","越南盾1000"),
    THB(CurrencyEnum.THB.getCode(),"THB","泰铢"),
    HKD(CurrencyEnum.HKD.getCode(),"HKD","港币"),
    IDR(CurrencyEnum.IDR.getCode(),"IDR","印度尼西亚盾1000"),
    EUR(CurrencyEnum.EUR.getCode(),"EUR","欧元"),
    SGD(CurrencyEnum.SGD.getCode(),"SGD","新加坡元"),
    JPY(CurrencyEnum.JPY.getCode(),"JPY","日元100"),
    KRW(CurrencyEnum.KRW.getCode(),"KRW","韩元"),
    AUD(CurrencyEnum.AUD.getCode(),"AUD","澳元"),
    BDT(CurrencyEnum.BDT.getCode(),"BDT","孟加拉国塔卡"),
    INR(CurrencyEnum.INR.getCode(),"INR","印度卢比"),
    BRL(CurrencyEnum.BRL.getCode(),"BRL","巴西雷亚尔"),
    USDT(CurrencyEnum.USDT.getCode(),"USDT","泰达币"),
    PHP(CurrencyEnum.PHP.getCode(),"PHP","菲律宾披索"),
    MXN(CurrencyEnum.MXN.getCode(),"MXN","墨西哥披索"),
    PKR(CurrencyEnum.PKR.getCode(),"PKR","巴基斯坦卢比"),
    EGP(CurrencyEnum.EGP.getCode(),"EGP","埃及镑"),
    ARS(CurrencyEnum.ARS.getCode(),"ARS","阿根廷披索"),
    RUB(CurrencyEnum.RUB.getCode(),"RUB","俄罗斯卢布"),
    TRY(CurrencyEnum.TRY.getCode(),"TRY","土耳其里拉"),
    KES(CurrencyEnum.KES.getCode(),"KES","肯尼亚先令"),
    LAK(CurrencyEnum.LAK.getCode(),"LAK","寮国基普1000"),
    NPR(CurrencyEnum.NPR.getCode(),"NPR","尼泊尔卢比"),
    PGK(CurrencyEnum.PGK.getCode(),"PGK","巴布亚新几内亚基那"),
    NGN(CurrencyEnum.NGN.getCode(),"NGN","奈及利亚奈拉"),
    BND(CurrencyEnum.BND.getCode(),"BND","汶莱元"),
//    TONU(CurrencyEnum.T.getCode(),"TONU","TONT USDT(加密货币)"),
    COP(CurrencyEnum.COP.getCode(),"COP","哥伦比亚披索"),
    UNKNOWN("UNKNOWN","UNKNOWN currency","未知货币");
    private final String code;
    private final String gameCurrencyCode;
    private final String fullName;


    public static CMDCurrencyEnums of(String code) {
        for (CMDCurrencyEnums ppCurrencyEnum : CMDCurrencyEnums.values()) {
            if (ppCurrencyEnum.code.equals(code)) {
                return ppCurrencyEnum;
            }
        }
        return UNKNOWN; // 异常
    }
}
