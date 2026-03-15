package com.cloud.baowang.play.api.enums.evo;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum EvoCurrencyEnum {

    AED("AED", "阿联酋迪拉姆", CurrencyEnum.AED.getCode()),
    AFN("AFN", "阿富汗尼", CurrencyEnum.AFN.getCode()),
    ALL("ALL", "阿尔巴尼亚列克", CurrencyEnum.ALL.getCode()),
    AMD("AMD", "亚美尼亚德拉姆", CurrencyEnum.AMD.getCode()),
    ANG("ANG", "荷属安的列斯盾", CurrencyEnum.ANG.getCode()),
    AOA("AOA", "安哥拉宽扎", CurrencyEnum.AOA.getCode()),
    ARS("ARS", "阿根廷比索", CurrencyEnum.ARS.getCode()),
    AWG("AWG", "阿鲁巴弗罗林", CurrencyEnum.AWG.getCode()),
    AZN("AZN", "阿塞拜疆马纳特", CurrencyEnum.AZN.getCode()),
    BAM("BAM", "波黑可兑换马克", CurrencyEnum.BAM.getCode()),
    BBD("BBD", "巴巴多斯元", CurrencyEnum.BBD.getCode()),
    BDT("BDT", "孟加拉塔卡", CurrencyEnum.BDT.getCode()),
    BGN("BGN", "保加利亚列弗", CurrencyEnum.BGN.getCode()),
    BHD("BHD", "巴林第纳尔", CurrencyEnum.BHD.getCode()),
    BIF("BIF", "布隆迪法郎", CurrencyEnum.BIF.getCode()),
    BMD("BMD", "百慕大元", CurrencyEnum.BMD.getCode()),
    BND("BND", "文莱元", CurrencyEnum.BND.getCode()),
    BOB("BOB", "玻利维亚诺", CurrencyEnum.BOB.getCode()),
    BRL("BRL", "巴西雷亚尔", CurrencyEnum.BRL.getCode()),
    BSD("BSD", "巴哈马元", CurrencyEnum.BSD.getCode()),
    BTN("BTN", "不丹努尔特鲁姆", CurrencyEnum.BTN.getCode()),
    BWP("BWP", "博茨瓦纳普拉", CurrencyEnum.BWP.getCode()),
    BYN("BYN", "白俄罗斯卢布", CurrencyEnum.BYN.getCode()),
    BZD("BZD", "伯利兹元", CurrencyEnum.BZD.getCode()),
    CAD("CAD", "加拿大元", CurrencyEnum.CAD.getCode()),
    CDF("CDF", "刚果法郎", CurrencyEnum.CDF.getCode()),
    CHF("CHF", "瑞士法郎", CurrencyEnum.CHF.getCode()),
    CLP("CLP", "智利比索", CurrencyEnum.CLP.getCode()),
    CNY("CNY", "人民币", CurrencyEnum.CNY.getCode()),
    COP("COP", "哥伦比亚比索", CurrencyEnum.COP.getCode()),
    CRC("CRC", "哥斯达黎加科朗", CurrencyEnum.CRC.getCode()),
    CVE("CVE", "佛得角埃斯库多", CurrencyEnum.CVE.getCode()),
    CZK("CZK", "捷克克朗", CurrencyEnum.CZK.getCode()),
    DJF("DJF", "吉布提法郎", CurrencyEnum.DJF.getCode()),
    DKK("DKK", "丹麦克朗", CurrencyEnum.DKK.getCode()),
    DOP("DOP", "多米尼加比索", CurrencyEnum.DOP.getCode()),
    DZD("DZD", "阿尔及利亚第纳尔", CurrencyEnum.DZD.getCode()),
    EGP("EGP", "埃及镑", CurrencyEnum.EGP.getCode()),
    ERN("ERN", "厄立特里亚纳克法", CurrencyEnum.ERN.getCode()),
    ETB("ETB", "埃塞俄比亚比尔", CurrencyEnum.ETB.getCode()),
    EUR("EUR", "欧元", CurrencyEnum.EUR.getCode()),
    FJD("FJD", "斐济元", CurrencyEnum.FJD.getCode()),
    FKP("FKP", "福克兰群岛镑", CurrencyEnum.FKP.getCode()),
    GBP("GBP", "英镑", CurrencyEnum.GBP.getCode()),
    GEL("GEL", "格鲁吉亚拉里", CurrencyEnum.GEL.getCode()),
    GHS("GHS", "加纳塞地", CurrencyEnum.GHS.getCode()),
    GIP("GIP", "直布罗陀镑", CurrencyEnum.GIP.getCode()),
    GMD("GMD", "冈比亚达拉西", CurrencyEnum.GMD.getCode()),
    GNF("GNF", "几内亚法郎", CurrencyEnum.GNF.getCode()),
    GTQ("GTQ", "危地马拉格查尔", CurrencyEnum.GTQ.getCode()),
    GYD("GYD", "圭亚那元", CurrencyEnum.GYD.getCode()),
    HKD("HKD", "港元", CurrencyEnum.HKD.getCode()),
    HNL("HNL", "洪都拉斯伦皮拉", CurrencyEnum.HNL.getCode()),
    HRK("HRK", "克罗地亚库纳", CurrencyEnum.HRK.getCode()),
    HTG("HTG", "海地古德", CurrencyEnum.HTG.getCode()),
    HUF("HUF", "匈牙利福林", CurrencyEnum.HUF.getCode()),
    IDR("IDR", "印尼卢比", CurrencyEnum.IDR.getCode()),
    ILS("ILS", "以色列新谢克尔", CurrencyEnum.ILS.getCode()),
    INR("INR", "印度卢比", CurrencyEnum.INR.getCode()),
    IQD("IQD", "伊拉克第纳尔", CurrencyEnum.IQD.getCode()),
    ISK("ISK", "冰岛克朗", CurrencyEnum.ISK.getCode()),
    JMD("JMD", "牙买加元", CurrencyEnum.JMD.getCode()),
    JOD("JOD", "约旦第纳尔", CurrencyEnum.JOD.getCode()),
    JPY("JPY", "日元", CurrencyEnum.JPY.getCode()),
    KES("KES", "肯尼亚先令", CurrencyEnum.KES.getCode()),
    KGS("KGS", "吉尔吉斯索姆", CurrencyEnum.KGS.getCode()),
    KHR("KHR", "柬埔寨瑞尔", CurrencyEnum.KHR.getCode()),
    KMF("KMF", "科摩罗法郎", CurrencyEnum.KMF.getCode()),
    KRW("KRW", "韩元", CurrencyEnum.KRW.getCode()),
    KWD("KWD", "科威特第纳尔", CurrencyEnum.KWD.getCode()),
    KYD("KYD", "开曼元", CurrencyEnum.KYD.getCode()),
    KZT("KZT", "哈萨克坚戈", CurrencyEnum.KZT.getCode()),
    LAK("LAK", "老挝基普", CurrencyEnum.LAK.getCode()),
    LBP("LBP", "黎巴嫩镑", CurrencyEnum.LBP.getCode()),
    LKR("LKR", "斯里兰卡卢比", CurrencyEnum.LKR.getCode()),
    LRD("LRD", "利比里亚元", CurrencyEnum.LRD.getCode()),
    LSL("LSL", "莱索托洛蒂", CurrencyEnum.LSL.getCode()),
    LYD("LYD", "利比亚第纳尔", CurrencyEnum.LYD.getCode()),
    MAD("MAD", "摩洛哥迪拉姆", CurrencyEnum.MAD.getCode()),
    MDL("MDL", "摩尔多瓦列伊", CurrencyEnum.MDL.getCode()),
    MGA("MGA", "马达加斯加阿里亚里", CurrencyEnum.MGA.getCode()),
    MKD("MKD", "马其顿第纳尔", CurrencyEnum.MKD.getCode()),
    MNT("MNT", "蒙古图格里克", CurrencyEnum.MNT.getCode()),
    MOP("MOP", "澳门元", CurrencyEnum.MOP.getCode()),
    MRU("MRU", "毛里塔尼亚乌吉亚", CurrencyEnum.MRU.getCode()),
    MUR("MUR", "毛里求斯卢比", CurrencyEnum.MUR.getCode()),
    MVR("MVR", "马尔代夫拉菲亚", CurrencyEnum.MVR.getCode()),
    MWK("MWK", "马拉维克瓦查", CurrencyEnum.MWK.getCode()),
    MXN("MXN", "墨西哥比索", CurrencyEnum.MXN.getCode()),
    MYR("MYR", "马来西亚林吉特", CurrencyEnum.MYR.getCode()),
    MZN("MZN", "莫桑比克梅蒂卡尔", CurrencyEnum.MZN.getCode()),
    NAD("NAD", "纳米比亚元", CurrencyEnum.NAD.getCode()),
    NGN("NGN", "尼日利亚奈拉", CurrencyEnum.NGN.getCode()),
    NIO("NIO", "尼加拉瓜科多巴", CurrencyEnum.NIO.getCode()),
    NOK("NOK", "挪威克朗", CurrencyEnum.NOK.getCode()),
    NPR("NPR", "尼泊尔卢比", CurrencyEnum.NPR.getCode()),
    NZD("NZD", "新西兰元", CurrencyEnum.NZD.getCode()),
    OMR("OMR", "阿曼里亚尔", CurrencyEnum.OMR.getCode()),
    PAB("PAB", "巴拿马巴波亚", CurrencyEnum.PAB.getCode()),
    PEN("PEN", "秘鲁索尔", CurrencyEnum.PEN.getCode()),
    PGK("PGK", "巴布亚新几内亚基那", CurrencyEnum.PGK.getCode()),
    PHP("PHP", "菲律宾比索", CurrencyEnum.PHP.getCode()),
    PKR("PKR", "巴基斯坦卢比", CurrencyEnum.PKR.getCode()),
    PLN("PLN", "波兰兹罗提", CurrencyEnum.PLN.getCode()),
    PYG("PYG", "巴拉圭瓜拉尼", CurrencyEnum.PYG.getCode()),
    QAR("QAR", "卡塔尔里亚尔", CurrencyEnum.QAR.getCode()),
    RON("RON", "罗马尼亚列伊", CurrencyEnum.RON.getCode()),
    RSD("RSD", "塞尔维亚第纳尔", CurrencyEnum.RSD.getCode()),
    RUB("RUB", "俄罗斯卢布", CurrencyEnum.RUB.getCode()),
    RWF("RWF", "卢旺达法郎", CurrencyEnum.RWF.getCode()),
    SAR("SAR", "沙特里亚尔", CurrencyEnum.SAR.getCode()),
    SBD("SBD", "所罗门群岛元", CurrencyEnum.SBD.getCode()),
    SCR("SCR", "塞舌尔卢比", CurrencyEnum.SCR.getCode()),
    SEK("SEK", "瑞典克朗", CurrencyEnum.SEK.getCode()),
    SGD("SGD", "新加坡元", CurrencyEnum.SGD.getCode()),
    SHP("SHP", "圣赫勒拿镑", CurrencyEnum.SHP.getCode()),
    SLL("SLL", "塞拉利昂利昂", CurrencyEnum.SLL.getCode()),
    SOS("SOS", "索马里先令", CurrencyEnum.SOS.getCode()),
    SRD("SRD", "苏里南元", CurrencyEnum.SRD.getCode()),
    SVC("SVC", "萨尔瓦多科朗", CurrencyEnum.SVC.getCode()),
    SZL("SZL", "斯威士兰里兰吉尼", CurrencyEnum.SZL.getCode()),
    THB("THB", "泰铢", CurrencyEnum.THB.getCode()),
    TJS("TJS", "塔吉克索莫尼", CurrencyEnum.TJS.getCode()),
    TMT("TMT", "土库曼马纳特", CurrencyEnum.TMT.getCode()),
    TND("TND", "突尼斯第纳尔", CurrencyEnum.TND.getCode()),
    TOP("TOP", "汤加潘加", CurrencyEnum.TOP.getCode()),
    TRY("TRY", "土耳其里拉", CurrencyEnum.TRY.getCode()),
    TTD("TTD", "特立尼达和多巴哥元", CurrencyEnum.TTD.getCode()),
    TZS("TZS", "坦桑尼亚先令", CurrencyEnum.TZS.getCode()),
    UAH("UAH", "乌克兰格里夫纳", CurrencyEnum.UAH.getCode()),
    UGX("UGX", "乌干达先令", CurrencyEnum.UGX.getCode()),
    USD("USD", "美元", CurrencyEnum.USD.getCode()),
    //
    USD2("USD", "美元", CurrencyEnum.USDT.getCode()),
    UYU("UYU", "乌拉圭比索", CurrencyEnum.UYU.getCode()),
    UZS("UZS", "乌兹别克苏姆", CurrencyEnum.UZS.getCode()),
    VED("VED", "委内瑞拉玻利瓦尔", CurrencyEnum.VED.getCode()),
    VES("VES", "委内瑞拉玻利瓦尔", CurrencyEnum.VES.getCode()),
    VND("VND", "越南盾", CurrencyEnum.VND.getCode()),
    VND2("VN2", "越南盾千", CurrencyEnum.KVND.getCode()),
    VUV("VUV", "瓦努阿图瓦图", CurrencyEnum.VUV.getCode()),
    WST("WST", "萨摩亚塔拉", CurrencyEnum.WST.getCode()),
    XAF("XAF", "中非法郎", CurrencyEnum.XAF.getCode()),
    XCD("XCD", "东加勒比元", CurrencyEnum.XCD.getCode()),
    XCG("XCG", "加勒比盾", CurrencyEnum.XCG.getCode()),
    XDR("XDR", "特别提款权", CurrencyEnum.XDR.getCode()),
    XOF("XOF", "西非法郎", CurrencyEnum.XOF.getCode()),
    XPF("XPF", "太平洋法郎", CurrencyEnum.XPF.getCode()),
    YER("YER", "也门里亚尔", CurrencyEnum.YER.getCode()),
    ZAR("ZAR", "南非兰特", CurrencyEnum.ZAR.getCode()),
    ZMW("ZMW", "赞比亚克瓦查", CurrencyEnum.ZMW.getCode());





    /*CNY("CNY", "人民幣", CurrencyEnum.CNY.getCode()),
    JPY("JPY", "日幣", CurrencyEnum.JPY.getCode()),
    THB("THB", "泰铢", CurrencyEnum.THB.getCode()),
    MMK("MMK", "缅元", CurrencyEnum.MMK.getCode()),

    MYR("MYR", "马来西亚令吉", CurrencyEnum.MYR.getCode()),
    IDR("IDR", "印尼盾", CurrencyEnum.IDR.getCode()),
    USD("USD", "美金", CurrencyEnum.USD.getCode()),
    KRW("KRW", "韩圆", CurrencyEnum.KRW.getCode()),

    EUR("EUR", "欧元", CurrencyEnum.EUR.getCode()),
    INR("INR", "印度卢比", CurrencyEnum.INR.getCode()),
    GBP("GBP", "英镑", CurrencyEnum.GBP.getCode()),
    AUD("AUD", "澳大利亚元", CurrencyEnum.AUD.getCode()),

    PHP("PHP", "菲律宾披索", CurrencyEnum.PHP.getCode()),

    kIDR("kIDR", "千印尼盾", null),
    kVND("VND(K)", "千越南盾", CurrencyEnum.KVND.getCode()),
    VND("VND", "千越南盾", CurrencyEnum.VND.getCode()),

    CQP1("CQP1", "虛擬貨幣", null),

    HKD("HKD", "港币", CurrencyEnum.HKD.getCode()),
    CQP2("CQP2", "虛擬貨幣", null),


    MMKP1("MMKP1", "虛擬貨幣", null),

    CFC("CFC", "虛擬貨幣", null),

    DIMC("DIMC", "鑽石幣", null),


    GODC("GODC", "黃金幣", null),

    RUB("RUB", "俄罗斯卢布", CurrencyEnum.RUB.getCode()),

    PLN("PLN", "波兰兹罗提", CurrencyEnum.PLN.getCode()),

    KHR("KHR", "柬埔寨瑞尔", CurrencyEnum.KHR.getCode()),

    YGB("YGB", "YGB", null),
    mBTC("mBTC", "比特幣", null),
    mLTC("mLTC", "萊特幣", null),


    mETH("mETH", "以太幣", null),
    EOS("EOS", "柚子幣", null),
    EGC("EGC", "EGC", null),

    kKHR("kKHR", "千柬埔寨瑞尔", null),
    MXN("MXN", "墨西哥披索", CurrencyEnum.MXN.getCode()),
    SEK("SEK", "瑞典克朗", CurrencyEnum.SEK.getCode()),

    NOK("NOK", "挪威克朗", CurrencyEnum.NOK.getCode()),
    CAD("CAD", "加拿大元", CurrencyEnum.CAD.getCode()),
    USDX("USDX", "USDX", null),
    EZGC("EZGC", "EZGC", null),
    BLC("BLC", "BLC", null),

    CT("CT", "CT", null),
    DCCB("DCCB", "DCCB", null),
    OTC("OTC", "OTC", null),
    DSCB("DSCB(K)", "DSCB(K)", null),
    HDA("HDA", "HDA", null),
    POC("POC", "POC", null),
    USDT("USDT", "USDT", CurrencyEnum.USDT.getCode()),
    BHD("BHD", "BHD", null),


    mBHD("mBHD", "mBHD", null),
    mEOS("mEOS", "mEOS", null),

    BRL("BRL", "巴西雷亚尔", CurrencyEnum.BRL.getCode()),
    KES("KES", "肯尼亚先令", null),


    WICKS("WICKS", "WICKS", null),
    BB("BB$", "BB$", null),
    BET("BET", "貝塔幣", null),
    HMMK("MMK(100)", "百缅元", null),
    INR1("INR(0.01)", "印度盧比(派薩)", null),

    TRY("TRY", "土耳其里拉", CurrencyEnum.TRY.getCode()),

    ZAR("ZAR", "南非兰特", CurrencyEnum.ZAR.getCode()),

    MGC("MGC", "MGC", null),

    BND("BND", "汶莱元", CurrencyEnum.BND.getCode()),

    KMMK("MMK(K)", "緬元(千)", null),
    COP("COP", "哥伦比亚披索", CurrencyEnum.COP.getCode()),

    BDT("BDT", "孟加拉塔卡", CurrencyEnum.BDT.getCode()),
    CLP("CLP", "智利披索", CurrencyEnum.CLP.getCode()),


    uBTC("uBTC", "uBTC", null),

    TRX("TRX", "TRX", null),

    DOGE("DOGE", "多吉幣(狗狗幣)", null),
    NPR("NPR", "尼泊尔卢比", CurrencyEnum.NPR.getCode()),
    TND("TND", "突尼斯第纳尔", CurrencyEnum.TND.getCode()),
    LAK("LAK", "寮国基普", CurrencyEnum.LAK.getCode()),
    KLAK("LAK(K)", "老撾幣(千)", null),


    VES("VES", "玻利瓦", CurrencyEnum.VES.getCode()),
    ARS("ARS", "阿根廷披索", CurrencyEnum.ARS.getCode()),

    NGN("NGN", "尼日利亚奈拉", CurrencyEnum.NGN.getCode()),
*/
    /*SGD("SGD", "新加坡元", CurrencyEnum.SGD.getCode()),
    DZD("DZD", "阿尔及利亚第纳尔", CurrencyEnum.DZD.getCode()),
    BMD("BMD", "百慕大元", CurrencyEnum.BMD.getCode()),
    BOB("BOB", "玻利维亚诺", CurrencyEnum.BOB.getCode()),
    BZD("BZD", "贝里斯元", CurrencyEnum.BZD.getCode()),
    LKR("LKR", "斯里兰卡卢比", CurrencyEnum.LKR.getCode()),
    HRK("HRK", "克罗地亚库纳", CurrencyEnum.HRK.getCode()),
    CZK("CZK", "捷克克朗", CurrencyEnum.CZK.getCode()),
    DKK("DKK", "丹麦克朗", CurrencyEnum.DKK.getCode()),
    DOP("DOP", "多明尼加比索", CurrencyEnum.DOP.getCode()),
    HUF("HUF", "匈牙利福林", CurrencyEnum.HUF.getCode()),
    ISK("ISK", "冰岛克朗", CurrencyEnum.ISK.getCode()),
    IRR("IRR", "伊朗里亚尔", CurrencyEnum.IRR.getCode()),
    IQD("IQD", "伊拉克第纳尔", CurrencyEnum.IQD.getCode()),
    ILS("ILS", "新谢克尔", CurrencyEnum.ILS.getCode()),

    KZT("KZT", "哈萨克斯坦坚戈", CurrencyEnum.KZT.getCode()),


    KWD("KWD", "科威特第纳尔", CurrencyEnum.KWD.getCode()),
    KGS("KGS", "吉尔吉斯索姆", CurrencyEnum.KGS.getCode()),

    LBP("LBP", "黎巴嫩镑", CurrencyEnum.LBP.getCode()),
    LSL("LSL", "莱索托洛蒂", CurrencyEnum.LSL.getCode()),
    LRD("LRD", "赖比瑞亚元", CurrencyEnum.LRD.getCode()),
    LYD("LYD", "利比亚第纳尔", CurrencyEnum.LYD.getCode()),
    ETB("ETB", "衣索比亚比尔", CurrencyEnum.ETB.getCode()),
    MOP("MOP", "澳门币", CurrencyEnum.MOP.getCode()),
    MVR("MVR", "马尔代夫拉菲亚", CurrencyEnum.MVR.getCode()),

    MNT("MNT", "图格里克", CurrencyEnum.MNT.getCode()),
    MDL("MDL", "摩尔多瓦列伊", CurrencyEnum.MDL.getCode()),
    MAD("MAD", "摩洛哥迪尔汗", CurrencyEnum.MAD.getCode()),
    OMR("OMR", "阿曼里亚尔", CurrencyEnum.OMR.getCode()),
    NAD("NAD", "纳米比亚元", CurrencyEnum.NAD.getCode()),

    NIO("NIO", "尼加拉瓜科多巴", CurrencyEnum.NIO.getCode()),
    NZD("NZD", "纽西兰元", CurrencyEnum.NZD.getCode()),


    PKR("PKR", "巴基斯坦卢比", CurrencyEnum.PKR.getCode()),
    PGK("PGK", "巴布亚新几内亚基那", CurrencyEnum.PGK.getCode()),
    PYG("PYG", "巴拉圭瓜拉尼", CurrencyEnum.PYG.getCode()),
    PEN("PEN", "秘鲁新索尔", CurrencyEnum.PEN.getCode()),

    SAR("SAR", "沙特里亚尔", CurrencyEnum.SAR.getCode()),
    SLL("SLL", "塞拉利昂币", CurrencyEnum.SLL.getCode()),



    CHF("CHF", "瑞士法郎", CurrencyEnum.CHF.getCode()),


    UGX("UGX", "乌干达先令", CurrencyEnum.UGX.getCode()),
    MKD("MKD", "马其顿第纳尔", CurrencyEnum.MKD.getCode()),
    EGP("EGP", "埃及镑", CurrencyEnum.EGP.getCode()),
    TZS("TZS", "坦桑尼亚先令", CurrencyEnum.TZS.getCode()),
    UYU("UYU", "乌拉圭披索", CurrencyEnum.UYU.getCode()),
    UZS("UZS", "乌兹别克索姆", CurrencyEnum.UZS.getCode()),
    SLE("SLE", "新塞拉利昂币", CurrencyEnum.SLE.getCode()),
    BYN("BYN", "白俄罗斯卢布", CurrencyEnum.BYN.getCode()),
    TMT("TMT", "土库曼马纳特", CurrencyEnum.TMT.getCode()),
    GHS("GHS", "迦纳塞地", CurrencyEnum.GHS.getCode()),
    VEF("VEF", "委内瑞拉玻利瓦尔富尔特", CurrencyEnum.VEF.getCode()),
    MZN("MZN", "莫三比克梅蒂卡尔", CurrencyEnum.MZN.getCode()),
    AZN("AZN", "亚塞拜然马纳特", CurrencyEnum.AZN.getCode()),
    RON("RON", "罗马尼亚列伊", CurrencyEnum.RON.getCode()),

    XAF("XAF", "中非金融合作法郎", CurrencyEnum.XAF.getCode()),
    XOF("XOF", "非洲金融共同体法郎", CurrencyEnum.XOF.getCode()),
    ZMW("ZMW", "尚比亚克瓦查", CurrencyEnum.ZMW.getCode()),
    SRD("SRD", "苏利南元", CurrencyEnum.SRD.getCode()),
    AOA("AOA", "安哥拉宽扎", CurrencyEnum.AOA.getCode()),
    BGN("BGN", "保加利亚列弗", CurrencyEnum.BGN.getCode()),
    CDF("CDF", "刚果法郎", CurrencyEnum.CDF.getCode()),
    BAM("BAM", "波赫马克", CurrencyEnum.BAM.getCode()),
    UAH("UAH", "乌克兰格里夫纳", CurrencyEnum.UAH.getCode()),
    GEL("GEL", "格鲁吉亚拉里", CurrencyEnum.GEL.getCode()),*/
    ;



    private final String code;
    private final String name;
    private final String platCurrencyCode;

    EvoCurrencyEnum(String code, String name, String platCurrencyCode) {
        this.code = code;
        this.name = name;
        this.platCurrencyCode = platCurrencyCode;
    }

    public static void main(String[] args) {
        EvoCurrencyEnum cq9CurrencyEnum = byPlatCurrencyCode("KVND");
        System.out.println(cq9CurrencyEnum);
    }
    /**
     * 根据平台币币种，匹配
     */
    public static EvoCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (EvoCurrencyEnum tmp : EvoCurrencyEnum.values()) {
            if (StringUtils.equals(platCurrencyCode,tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 根据CQ9币种，匹配
     */
    public static EvoCurrencyEnum getCurrencyEnum(String currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        EvoCurrencyEnum[] types = EvoCurrencyEnum.values();
        for (EvoCurrencyEnum type : types) {
            if (currencyCode.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }



}
