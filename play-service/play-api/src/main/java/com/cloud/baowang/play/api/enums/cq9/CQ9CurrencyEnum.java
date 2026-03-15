package com.cloud.baowang.play.api.enums.cq9;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum CQ9CurrencyEnum {
    CNY("CNY", "人民幣", CurrencyEnum.CNY.getCode()),
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
    AED("AED", "阿联迪拉姆", CurrencyEnum.AED.getCode()),

    VES("VES", "玻利瓦", CurrencyEnum.VES.getCode()),
    ARS("ARS", "阿根廷披索", CurrencyEnum.ARS.getCode()),

    NGN("NGN", "尼日利亚奈拉", CurrencyEnum.NGN.getCode()),

    AMD("AMD", "亚美尼亚德拉姆", CurrencyEnum.AMD.getCode()),



    //**




    SGD("SGD", "新加坡元", CurrencyEnum.SGD.getCode()),





    ALL("ALL", "阿尔巴尼亚列克", CurrencyEnum.ALL.getCode()),
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
    GEL("GEL", "格鲁吉亚拉里", CurrencyEnum.GEL.getCode());



    private final String code;
    private final String name;
    private final String platCurrencyCode;

    CQ9CurrencyEnum(String code, String name, String platCurrencyCode) {
        this.code = code;
        this.name = name;
        this.platCurrencyCode = platCurrencyCode;
    }

    public static void main(String[] args) {
        CQ9CurrencyEnum cq9CurrencyEnum = byPlatCurrencyCode("KVND");
        System.out.println(cq9CurrencyEnum);
    }
    /**
     * 根据平台币币种，匹配
     */
    public static CQ9CurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (CQ9CurrencyEnum tmp : CQ9CurrencyEnum.values()) {
            if (StringUtils.equals(platCurrencyCode,tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 根据CQ9币种，匹配
     */
    public static CQ9CurrencyEnum getCurrencyEnum(String currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        CQ9CurrencyEnum[] types = CQ9CurrencyEnum.values();
        for (CQ9CurrencyEnum type : types) {
            if (currencyCode.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


}
