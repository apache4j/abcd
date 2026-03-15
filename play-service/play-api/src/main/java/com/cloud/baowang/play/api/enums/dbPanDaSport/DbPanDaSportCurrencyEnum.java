package com.cloud.baowang.play.api.enums.dbPanDaSport;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.play.api.enums.dbDj.DbDjCurrencyEnum;
import lombok.Getter;

@Getter
public enum DbPanDaSportCurrencyEnum {

    CNY(1, "CNY", CurrencyEnum.CNY.getCode(), "人民币"),
    USD(2, "USD", CurrencyEnum.USD.getCode(), "美元"),
    HKD(3, "HKD", CurrencyEnum.HKD.getCode(), "港币"),
    VND1000(4, "VND1000", CurrencyEnum.KVND.getCode(), "越南盾(汇率/1000)"),
    VND(23, "VND", CurrencyEnum.VND.getCode(), "越南盾(原始汇率)"),
    SGD(5, "SGD", CurrencyEnum.SGD.getCode(), "新加坡币"),
    GBP(6, "GBP", CurrencyEnum.GBP.getCode(), "英镑"),
    EUR(7, "EUR", CurrencyEnum.EUR.getCode(), "欧元"),
    TWD(9, "TWD", CurrencyEnum.TWD.getCode(), "台币"),
    JPY(10, "JPY", CurrencyEnum.JPY.getCode(), "日元"),
    PHP(11, "PHP", CurrencyEnum.PHP.getCode(), "菲律宾peso"),
    KRW(12, "KRW", CurrencyEnum.KRW.getCode(), "韩元"),
    AUD(13, "AUD", CurrencyEnum.AUD.getCode(), "澳元"),
    CAD(14, "CAD", CurrencyEnum.CAD.getCode(), "加元"),
    AED(15, "AED", CurrencyEnum.AED.getCode(), "阿联酋迪拉姆"),
    MOP(16, "MOP", CurrencyEnum.MOP.getCode(), "澳门元"),
    DZD(17, "DZD", CurrencyEnum.DZD.getCode(), "阿尔及利亚第纳尔"),
    OMR(18, "OMR", CurrencyEnum.OMR.getCode(), "阿曼里亚尔"),
    EGP(19, "EGP", CurrencyEnum.EGP.getCode(), "埃及镑"),
    RUB(20, "RUB", CurrencyEnum.RUB.getCode(), "俄罗斯卢布"),
//    IDR1000(21, "IDR1000", CurrencyEnum.IDR1000.getCode(), "印尼盾(汇率/1000)"),
    IDR(25, "IDR", CurrencyEnum.IDR.getCode(), "印尼盾(原始汇率)"),
    MYR(22, "MYR", CurrencyEnum.MYR.getCode(), "马来西亚林吉特"),
    INR(24, "INR", CurrencyEnum.INR.getCode(), "印度卢比"),
    THB(26, "THB", CurrencyEnum.THB.getCode(), "泰铢"),
    BND(27, "BND", CurrencyEnum.BND.getCode(), "文莱林吉特"),
    BRL(29, "BRL", CurrencyEnum.BRL.getCode(), "巴西雷亚尔"),
    AZN(41, "AZN", CurrencyEnum.AZN.getCode(), "亚塞拜然马纳特"),
    BDT(44, "BDT", CurrencyEnum.BDT.getCode(), "孟加拉国塔卡"),
    MXN(116, "MXN", CurrencyEnum.MXN.getCode(), "墨西哥比索"),
    NGN(119, "NGN", CurrencyEnum.NGN.getCode(), "尼日利亚奈拉"),
    ZAR(172, "ZAR", CurrencyEnum.ZAR.getCode(), "南非兰特"),
    ZMW(173, "ZMW", CurrencyEnum.ZMW.getCode(), "赞比亚克瓦查"),
//    MMK1000(182, "MMK1000", CurrencyEnum.MMK1000.getCode(), "缅元(汇率/1000)"),
    MMK(33, "MMK", CurrencyEnum.MMK.getCode(), "緬甸元（原始汇率）"),
    COP(30, "COP", CurrencyEnum.COP.getCode(), "哥伦比亚比索"),
    TRY(31, "TRY", CurrencyEnum.TRY.getCode(), "土耳其里拉"),
    XOF(167, "XOF", CurrencyEnum.XOF.getCode(), "西非法郎"),
    USDT(201, "USDT", CurrencyEnum.USDT.getCode(), "泰达币"),
    PKR(127, "PKR", CurrencyEnum.PKR.getCode(), "巴基斯坦卢比"),
    ETB(70, "ETB", CurrencyEnum.ETB.getCode(), "埃塞俄比亚比尔"),
//    LAK1000(196, "LAK1000", CurrencyEnum.LAK1000.getCode(), "老挝基普(汇率/1000)"),
    NPR(122, "NPR", CurrencyEnum.NPR.getCode(), "尼泊尔卢比"),
    UZS(158, "UZS", CurrencyEnum.UZS.getCode(), "乌兹别克斯坦索姆"),
    PEN(125, "PEN", CurrencyEnum.PEN.getCode(), "秘鲁索尔"),
    KES(93, "KES", CurrencyEnum.KES.getCode(), "肯尼亚先令"),
    ARS(39, "ARS", CurrencyEnum.ARS.getCode(), "阿根廷比索"),
    KWD(98, "KWD", CurrencyEnum.KWD.getCode(), "科威特第纳尔"),
    MVR(114, "MVR", CurrencyEnum.MVR.getCode(), "马尔地夫拉菲亚"),
    KHR(95, "KHR", CurrencyEnum.KHR.getCode(), "柬埔寨瑞尔"),
    CLP(59, "CLP", CurrencyEnum.CLP.getCode(), "智利比索"),
//    CLP1000(190, "CLP1000", CurrencyEnum.CLP1000.getCode(), "智利比索(汇率/1000)"),
    XAF(162, "XAF", CurrencyEnum.XAF.getCode(), "中非法郎");

    private Integer code;
    private String currencyCode;
    private String platCurrencyCode;
    private String name;

    DbPanDaSportCurrencyEnum(Integer code, String currencyCode, String platCurrencyCode, String name) {
        this.code = code;
        this.currencyCode = currencyCode;
        this.platCurrencyCode = platCurrencyCode;
        this.name = name;
    }

    public static DbPanDaSportCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (DbPanDaSportCurrencyEnum tmp : DbPanDaSportCurrencyEnum.values()) {
            if (platCurrencyCode.equals(tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }

    public static DbPanDaSportCurrencyEnum fromCode(Integer code) {
        if (code == null) return null;
        for (DbPanDaSportCurrencyEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
