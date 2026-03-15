package com.cloud.baowang.play.api.enums.sb;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum SBCurrencyEnum {

    MYR(2, "MYR", CurrencyEnum.MYR.getCode(), "马元", "1:1"),
    USD(3, "USD", CurrencyEnum.USD.getCode(), "美元", "1:1"),
    THB(4, "THB", CurrencyEnum.THB.getCode(), "泰铢", "1:1"),
    EUR(6, "EUR", CurrencyEnum.EUR.getCode(), "欧元", "1:1"),
    GBP(12, "GBP", CurrencyEnum.GBP.getCode(), "英镑", "1:1"),
    RMB(13, "RMB", CurrencyEnum.CNY.getCode(), "人民币", "1:1"),
    IDR(15, "IDR", CurrencyEnum.IDR.getCode(), "印度尼西亚盾", "1:1000"),
    UUS(20, "UUS", "SBA-TEST-CURRENCY", "测试货币(测试环境使用)", "-"),
    JPY(32, "JPY", CurrencyEnum.JPY.getCode(), "日圆", "1:1"),
    CHF(41, "CHF", CurrencyEnum.CHF.getCode(), "瑞士法郎", "1:1"),
    PHP(42, "PHP", CurrencyEnum.PHP.getCode(), "菲律宾比索", "1:1"),
    WON(45, "WON", CurrencyEnum.KRW.getCode(), "韩元", "1:1"),
    BND(46, "BND", CurrencyEnum.BND.getCode(), "文莱元", "1:1"),
    ZAR(48, "ZAR", CurrencyEnum.ZAR.getCode(), "南非兰特", "1:1"),
    MXN(49, "MXN", CurrencyEnum.MXN.getCode(), "墨西哥比绍", "1:1"),
    CAD(50, "CAD", CurrencyEnum.CAD.getCode(), "加币", "1:1"),
    INH_VND(51, "INH(VND)", CurrencyEnum.KVND.getCode(), "越南盾", "1:1000"),
    DKK(52, "DKK", CurrencyEnum.DKK.getCode(), "丹麦克朗", "1:1"),
    SEK(53, "SEK", CurrencyEnum.SEK.getCode(), "瑞典克朗", "1:1"),
    NOK(54, "NOK", CurrencyEnum.NOK.getCode(), "挪威克朗", "1:1"),
    RUB(55, "RUB", CurrencyEnum.RUB.getCode(), "俄罗斯卢布", "1:1"),
    PLN(56, "PLN", CurrencyEnum.PLN.getCode(), "波兰兹罗提", "1:1"),
    CZK(57, "CZK", CurrencyEnum.CZK.getCode(), "捷克克朗", "1:1"),
    RON(58, "RON", CurrencyEnum.RON.getCode(), "罗马尼亚列伊", "1:1"),
    INR(61, "INR", CurrencyEnum.INR.getCode(), "印度卢比", "1:1"),
    MMK(70, "MMK(MKK)", CurrencyEnum.MMK.getCode(), "缅甸元", "1:1000"),
    KHR(71, "KHR", CurrencyEnum.KHR.getCode(), "柬埔寨瑞尔", "1:1000"),
    LIR(73, "LIR(TRY)", CurrencyEnum.TRL.getCode(), "土耳其里拉", "1:1"),
    KES(79, "KES", CurrencyEnum.KES.getCode(), "肯尼亚先令", "1:1"),
    GHS(80, "GHS", CurrencyEnum.GHS.getCode(), "加纳塞地", "1:1"),
    BRL(82, "BRL", CurrencyEnum.BRL.getCode(), "巴西雷亚尔", "1:1"),
    CLP(83, "CLP", CurrencyEnum.CLP.getCode(), "智利比索", "1:1"),
    COP(84, "COP", CurrencyEnum.COP.getCode(), "哥伦比亚比索", "1:1"),
    PEN(85, "PEN", CurrencyEnum.PEN.getCode(), "秘鲁新索尔", "1:1"),
    ARS(86, "ARS", CurrencyEnum.ARS.getCode(), "阿根廷比索", "1:1"),
    AED(90, "AED", CurrencyEnum.AED.getCode(), "阿联酋迪拉姆", "1:1"),
    LAK(93, "LAK", CurrencyEnum.LAK.getCode(), "老挝基普(寮国基普)", "1:1000"),
    USDT(96, "USDT", CurrencyEnum.USDT.getCode(), "USDT", "1:1"),
    BDT(97, "BDT", CurrencyEnum.BDT.getCode(), "孟加拉塔卡", "1:1"),
    PKR(121, "PKR", CurrencyEnum.PKR.getCode(), "巴基斯坦卢比", "1:1"),
    KZT(122, "KZT", CurrencyEnum.KZT.getCode(), "哈萨克坦吉", "1:1"),
    NPR(123, "NPR", CurrencyEnum.NPR.getCode(), "尼泊尔卢比", "1:1"),
    RMB2(124, "RMB2", CurrencyEnum.CNY.getCode(), "人民币(只支援沙巴白牌中国版)", "1:1"),
    INH2_VND(125, "INH2(VND)", CurrencyEnum.KVND.getCode(), "越南盾(只支援沙巴白牌中国版)", "1:1000"),
    TB2(126, "TB2", null, "泰铢(只支援沙巴白牌中国版)", "1:1"),
    INR2(127, "INR2", null, "印度卢比(只支援沙巴白牌中国版)", "1:1"),
    PE2(128, "PE2", null, "菲律宾比索(只支援沙巴白牌中国版)", "1:1"),
    IN2_IDR(129, "IN2(IDR)", CurrencyEnum.IDR.getCode(), "印度尼西亚盾(只支援沙巴白牌中国版)", "1:1000"),
    WON2(130, "Won2", null, "韩元(只支援沙巴白牌中国版)", "1:1"),
    RM2(131, "RM2", null, "马元(只支援沙巴白牌中国版)", "1:1"),
    US_DOLLAR2(132, "US$2", null, "美元(只支援沙巴白牌中国版)", "1:1"),
    TND(133, "TND", null, "突尼斯第纳尔", "1:1"),
    ZMW(134, "ZMW", null, "赞比亚克瓦查", "1:1"),
    TZS(135, "TZS", null, "坦桑尼亚先令", "1:1"),
    JPY2(203, "JPY2", null, "日圆(只支援沙巴白牌中国版)", "1:1"),
    EUR2(204, "EUR2", null, "欧元(只支援沙巴白牌中国版)", "1:1"),
    BRL2(205, "BRL2", CurrencyEnum.BRL.getCode(), "巴西雷亚尔(只支援沙巴白牌中国版)", "1:1"),
    USDT2(206, "USDT2", null, "USDT2(只支援沙巴白牌中国版)", "1:1"),
    NGN(207, "NGN", null, "尼日利亚奈拉", "1:1"),
    DZD(210, "DZD", null, "阿尔及利亚第纳尔", "1:1"),
    MAD(211, "MAD", null, "摩洛哥迪拉姆", "1:1"),
    FRF(212, "FRF", null, "法国法郎", "1:1"),
    BDT2(215, "BDT2", null, "孟加拉国塔卡(只支援沙巴白牌中国版)", "1:1"),
    EGP(216, "EGP", null, "埃及镑", "1:1"),
    LBP(217, "LBP", null, "黎巴嫩鎊", "1:1000");

    private final int code;
    private final String currencyCode;
    private final String platformCurrencyCode;
    private final String name;
    private final String exchangeRate;


    SBCurrencyEnum(int code, String currencyCode, String platformCurrencyCode, String name, String exchangeRate) {
        this.code = code;
        this.currencyCode = currencyCode;
        this.platformCurrencyCode = platformCurrencyCode;
        this.name = name;
        this.exchangeRate = exchangeRate;
    }

    public String getPlatformCurrencyCode() {
        return platformCurrencyCode;
    }

    public int getCode() {
        return code;
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

    public static SBCurrencyEnum getSBCurrencyEnum(Integer code) {
        if (null == code) {
            return null;
        }
        SBCurrencyEnum[] types = SBCurrencyEnum.values();
        for (SBCurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<String> getPlatCurrencyCodeEnumList(Integer code) {
        List<String> list = Lists.newArrayList();
        if (null == code) {
            return list;
        }

        SBCurrencyEnum[] types = SBCurrencyEnum.values();
        for (SBCurrencyEnum type : types) {
            if (code.equals(type.getCode()) && StringUtils.isNotBlank(type.getPlatformCurrencyCode())) {
                list.add(type.getPlatformCurrencyCode());
            }
        }
        return list;
    }

    public static SBCurrencyEnum getByPlatformCurrencyCode(String platformCurrencyCode) {
        if (null == platformCurrencyCode) {
            return null;
        }
        SBCurrencyEnum[] types = SBCurrencyEnum.values();
        for (SBCurrencyEnum type : types) {
            if (type.getPlatformCurrencyCode() == null) {
                continue;
            }
            if (platformCurrencyCode.equals(type.getPlatformCurrencyCode())) {
                return type;
            }
        }
        return null;
    }

}
