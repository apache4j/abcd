package com.cloud.baowang.play.api.enums.dbDj;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.play.api.enums.ftg.FTGCurrencyEnum;
import lombok.Getter;

/**
 * DJ 平台币种枚举
 */
@Getter
public enum DbDjCurrencyEnum {

    CNY(1, "CNY", CurrencyEnum.CNY.getCode(), "人民币"),
    USD(2, "USD", CurrencyEnum.USD.getCode(), "美元"),
    HKD(3, "HKD", CurrencyEnum.HKD.getCode(), "港元"),
    VND1000(4, "VND1000", CurrencyEnum.KVND.getCode(), "越南盾1000"),
    SGD(5, "SGD", CurrencyEnum.SGD.getCode(), "新加坡元"),
    GBP(6, "GBP", CurrencyEnum.GBP.getCode(), "英镑"),
    EUR(7, "EUR", CurrencyEnum.EUR.getCode(), "欧元"),
    TWD(9, "TWD", CurrencyEnum.TWD.getCode(), "新台币"),
    JPY(10, "JPY", CurrencyEnum.JPY.getCode(), "日元"),
    PHP(11, "PHP", CurrencyEnum.PHP.getCode(), "菲律宾比索"),
    KRW(12, "KRW", CurrencyEnum.KRW.getCode(), "韩元"),
    AUD(13, "AUD", CurrencyEnum.AUD.getCode(), "澳大利亚元"),
    CAD(14, "CAD", CurrencyEnum.CAD.getCode(), "加元"),
    AED(15, "AED", CurrencyEnum.AED.getCode(), "阿联酋迪拉姆"),
    MOP(16, "MOP", CurrencyEnum.MOP.getCode(), "澳门元"),
    DZD(17, "DZD", CurrencyEnum.DZD.getCode(), "阿尔及利亚第纳尔"),
    OMR(18, "OMR", CurrencyEnum.OMR.getCode(), "阿曼里亚尔"),
    EGP(19, "EGP", CurrencyEnum.EGP.getCode(), "埃及镑"),
    RUB(20, "RUB", CurrencyEnum.RUB.getCode(), "俄罗斯卢布"),
//    IDR1000(21, "IDR1000", CurrencyEnum.IDR.getCode(), "印尼盾1000"),
    MYR(22, "MYR", CurrencyEnum.MYR.getCode(), "马来西亚林吉特"),
    INR(24, "INR", CurrencyEnum.INR.getCode(), "印度卢比"),
    THB(26, "THB", CurrencyEnum.THB.getCode(), "泰铢"),
    BND(27, "BND", CurrencyEnum.BND.getCode(), "文莱元"),
//    CEO(28, "CEO", CurrencyEnum.CEO.getCode(), "测试币"),
    BRL(29, "BRL", CurrencyEnum.BRL.getCode(), "巴西雷亚尔"),
    MMK(33, "MMK", CurrencyEnum.MMK.getCode(), "缅甸元"),
    MXN(116, "MXN", CurrencyEnum.MXN.getCode(), "墨西哥比索"),
    ZAR(172, "ZAR", CurrencyEnum.ZAR.getCode(), "南非兰特"),
//    KRW1000(185, "KRW1000", CurrencyEnum.KRW.getCode(), "韩元1000"),
    COP(30, "COP", CurrencyEnum.COP.getCode(), "哥伦比亚比索"),
    TRY(31, "TRY", CurrencyEnum.TRY.getCode(), "土耳其里拉"),
    AOA(38, "AOA", CurrencyEnum.AOA.getCode(), "安哥拉宽扎"),
    ARS(39, "ARS", CurrencyEnum.ARS.getCode(), "阿根廷比索"),
    BOB(49, "BOB", CurrencyEnum.BOB.getCode(), "玻利维亚诺"),
    CLP(59, "CLP", CurrencyEnum.CLP.getCode(), "智利比索"),
    CRC(61, "CRC", CurrencyEnum.CRC.getCode(), "哥斯达黎加科朗"),
    CUC(62, "CUC", CurrencyEnum.CUC.getCode(), "古巴可兑换比索"),
    CUP(63, "CUP", CurrencyEnum.CUP.getCode(), "古巴比索"),
    DOP(68, "DOP", CurrencyEnum.DOP.getCode(), "多米尼加比索"),
    GTQ(79, "GTQ", CurrencyEnum.GTQ.getCode(), "危地马拉格查尔"),
    MZN(117, "MZN", CurrencyEnum.MZN.getCode(), "莫桑比克梅蒂卡尔"),
    NIO(120, "NIO", CurrencyEnum.NIO.getCode(), "尼加拉瓜科多巴"),
    PAB(124, "PAB", CurrencyEnum.PAB.getCode(), "巴拿马巴波亚"),
    PEN(125, "PEN", CurrencyEnum.PEN.getCode(), "秘鲁索尔"),
    PYG(129, "PYG", CurrencyEnum.PYG.getCode(), "巴拉圭瓜拉尼"),
    SVC(146, "SVC", CurrencyEnum.SVC.getCode(), "萨尔瓦多科朗"),
    UYU(157, "UYU", CurrencyEnum.UYU.getCode(), "乌拉圭比索"),
    VES(159, "VES", CurrencyEnum.VES.getCode(), "委内瑞拉玻利瓦尔"),
//    COP1000(189, "COP1000", CurrencyEnum.COP.getCode(), "哥伦比亚比索1000"),
//    MMK1000(182, "MMK1000", CurrencyEnum.MMK.getCode(), "缅甸元1000"),
    ZWL(174, "ZWL", CurrencyEnum.ZWL.getCode(), "津巴布韦元"),
    ZMW(173, "ZMW", CurrencyEnum.ZMW.getCode(), "赞比亚克瓦查"),
    YER(171, "YER", CurrencyEnum.YER.getCode(), "也门里亚尔"),
    XPF(169, "XPF", CurrencyEnum.XPF.getCode(), "太平洋法郎"),
    XOF(167, "XOF", CurrencyEnum.XOF.getCode(), "西非法郎"),
    XCD(165, "XCD", CurrencyEnum.XCD.getCode(), "东加勒比元"),
    XAF(162, "XAF", CurrencyEnum.XAF.getCode(), "中非法郎"),
    WST(161, "WST", CurrencyEnum.WST.getCode(), "萨摩亚塔拉"),
    VUV(160, "VUV", CurrencyEnum.VUV.getCode(), "瓦努阿图瓦图"),
//    UZS1000(193, "UZS1000", CurrencyEnum.UZS.getCode(), "乌兹别克斯坦苏姆1000"),
//    UGX1000(175, "UGX1000", CurrencyEnum.UGX.getCode(), "乌干达先令1000"),
//    TZS1000(176, "TZS1000", CurrencyEnum.TZS.getCode(), "坦桑尼亚先令1000"),
    TTD(153, "TTD", CurrencyEnum.TTD.getCode(), "特立尼达和多巴哥元"),
    TOP(152, "TOP", CurrencyEnum.TOP.getCode(), "汤加潘加"),
    TND(151, "TND", CurrencyEnum.TND.getCode(), "突尼斯第纳尔"),
    TMT(150, "TMT", CurrencyEnum.TMT.getCode(), "土库曼斯坦新马纳特"),
    TJS(149, "TJS", CurrencyEnum.TJS.getCode(), "塔吉克索莫尼"),
    SZL(148, "SZL", CurrencyEnum.SZL.getCode(), "斯威士兰里兰吉尼"),
//    SYP1000(177, "SYP1000", CurrencyEnum.SYP.getCode(), "叙利亚镑1000"),
    STN(145, "STN", CurrencyEnum.STN.getCode(), "圣多美与普林西比多布拉"),
//    STD1000(194, "STD1000", CurrencyEnum.STD.getCode(), "圣多美多布拉1000"),
    SSP(143, "SSP", CurrencyEnum.SSP.getCode(), "南苏丹镑"),
    SRD(142, "SRD", CurrencyEnum.SRD.getCode(), "苏里南元"),
    SOS(141, "SOS", CurrencyEnum.SOS.getCode(), "索马里先令"),
    SLL1000(195, "SLL1000", CurrencyEnum.SLL.getCode(), "塞拉利昂利昂1000"),
    SHP(139, "SHP", CurrencyEnum.SHP.getCode(), "圣赫伦那镑"),
    SEK(138, "SEK", CurrencyEnum.SEK.getCode(), "瑞典克朗"),
    SDG(137, "SDG", CurrencyEnum.SDG.getCode(), "苏丹镑"),
    SAR(134, "SAR", CurrencyEnum.SAR.getCode(), "沙特里亚尔"),
    RSD(132, "RSD", CurrencyEnum.RSD.getCode(), "塞尔维亚戴纳"),
    RON(131, "RON", CurrencyEnum.RON.getCode(), "罗马尼亚列伊"),
    QAR(130, "QAR", CurrencyEnum.QAR.getCode(), "卡塔尔里亚尔"),
    PLN(128, "PLN", CurrencyEnum.PLN.getCode(), "波兰兹罗提"),
    PKR(127, "PKR", CurrencyEnum.PKR.getCode(), "巴基斯坦卢比"),
    NZD(123, "NZD", CurrencyEnum.NZD.getCode(), "新西兰元"),
    NOK(121, "NOK", CurrencyEnum.NOK.getCode(), "挪威克朗"),
    NGN(119, "NGN", CurrencyEnum.NGN.getCode(), "尼日利亚奈拉"),
    NAD(118, "NAD", CurrencyEnum.NAD.getCode(), "纳米比亚元"),
    USDT(201, "USDT", CurrencyEnum.USDT.getCode(), "泰达币");



    private final Integer code;
    private final String name;
    private final String platCurrencyCode;
    private final String desc;

    DbDjCurrencyEnum(Integer code, String name, String platCurrencyCode,String desc) {
        this.code = code;
        this.name = name;
        this.platCurrencyCode = platCurrencyCode;
        this.desc = desc;
    }


    public static DbDjCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (DbDjCurrencyEnum tmp : DbDjCurrencyEnum.values()) {
            if (platCurrencyCode.equals(tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }



    public static DbDjCurrencyEnum getCurrencyEnum(Integer currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        DbDjCurrencyEnum[] types = DbDjCurrencyEnum.values();
        for (DbDjCurrencyEnum type : types) {
            if (currencyCode.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

}


