package com.cloud.baowang.play.api.enums.pg;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum PGCurrencyEnum {
    AED("AED", "阿拉伯联合酋长国迪拉姆", CurrencyEnum.AED.getCode()),
    AFN("AFN", "阿富汗", CurrencyEnum.AFN.getCode()),
    ALL("ALL", "阿尔巴尼亚列克", CurrencyEnum.ALL.getCode()),
    AMD("AMD", "亚美尼亚德拉姆", CurrencyEnum.AMD.getCode()),
    ANG("ANG", "安的列斯盾", CurrencyEnum.ANG.getCode()),
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
    BND("BND", "汶莱元", CurrencyEnum.BND.getCode()),
    BOB("BOB", "玻利维亚诺", CurrencyEnum.BOB.getCode()),
    BRL("BRL", "巴西里亚伊（雷亚尔）", CurrencyEnum.BRL.getCode()),
    BSD("BSD", "巴哈马元", CurrencyEnum.BSD.getCode()),
    BTN("BTN", "不丹努尔特鲁姆", CurrencyEnum.BTN.getCode()),
    BWP("BWP", "博茨瓦纳普拉", CurrencyEnum.BWP.getCode()),
    BYN("BYN", "白俄罗斯卢布", CurrencyEnum.BYN.getCode()),
    BYR("BYR", "白俄罗斯卢布", CurrencyEnum.BYR.getCode()),
    BZD("BZD", "伯利兹元", CurrencyEnum.BZD.getCode()),
    CAD("CAD", "加拿大元", CurrencyEnum.CAD.getCode()),
    CDF("CDF", "刚果法郎", CurrencyEnum.CDF.getCode()),
    CHF("CHF", "瑞士法郎", CurrencyEnum.CHF.getCode()),
    CLP("CLP", "智利比索", CurrencyEnum.CLP.getCode()),
    CNY("CNY", "人民币", CurrencyEnum.CNY.getCode()),
    COP("COP", "哥伦比亚比索", CurrencyEnum.COP.getCode()),
    CRC("CRC", "哥斯达黎加科朗", CurrencyEnum.CRC.getCode()),
    CSD("CSD", "塞爾維亞第納爾", CurrencyEnum.CSD.getCode()),
    CUP("CUP", "古巴比索", CurrencyEnum.CUP.getCode()),
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
    FKP("FKP", "福克兰岛磅", CurrencyEnum.FKP.getCode()),
    GBP("GBP", "英镑", CurrencyEnum.GBP.getCode()),
    GEL("GEL", "格鲁吉亚拉里", CurrencyEnum.GEL.getCode()),
    GHS("GHS", "加纳塞地", CurrencyEnum.GHS.getCode()),
    GIP("GIP", "直布罗陀庞德", CurrencyEnum.GIP.getCode()),
    GMD("GMD", "冈比亚货币", CurrencyEnum.GMD.getCode()),
    GNF("GNF", "几内亚法郎", CurrencyEnum.GNF.getCode()),
    GTQ("GTQ", "危地马拉格查尔", CurrencyEnum.GTQ.getCode()),
    GYD("GYD", "圭亚那元", CurrencyEnum.GYD.getCode()),
    HNL("HNL", "洪都拉斯伦皮拉", CurrencyEnum.HNL.getCode()),
    HTG("HTG", "海地古德", CurrencyEnum.HTG.getCode()),
    HUF("HUF", "匈牙利福林", CurrencyEnum.HUF.getCode()),
    IDR("IDR", "印度尼西亚卢比盾", CurrencyEnum.IDR.getCode()),
    ILS("ILS", "以色列谢克尔", CurrencyEnum.ILS.getCode()),
    INR("INR", "印度卢比", CurrencyEnum.INR.getCode()),
    IQD("IQD", "伊拉克第纳尔", CurrencyEnum.IQD.getCode()),
    IRR("IRR", "伊朗里亚尔", CurrencyEnum.IRR.getCode()),
    ISK("ISK", "冰岛克朗", CurrencyEnum.ISK.getCode()),
    JMD("JMD", "牙买加元", CurrencyEnum.JMD.getCode()),
    JOD("JOD", "约旦第纳尔", CurrencyEnum.JOD.getCode()),
    JPY("JPY", "日元", CurrencyEnum.JPY.getCode()),
    KES("KES", "肯尼亚先令", CurrencyEnum.KES.getCode()),
    KGS("KGS", "吉尔吉斯斯坦索姆", CurrencyEnum.KGS.getCode()),
    KHR("KHR", "柬埔寨利尔斯", CurrencyEnum.KHR.getCode()),
    KMF("KMF", "科摩罗法郎", CurrencyEnum.KMF.getCode()),
    KPW("KPW", "北朝鲜元", CurrencyEnum.KPW.getCode()),
    KRW("KRW", "韩元", CurrencyEnum.KRW.getCode()),
    KWD("KWD", "科威特第纳尔", CurrencyEnum.KWD.getCode()),
    KYD("KYD", "开曼岛元", CurrencyEnum.KYD.getCode()),
    KZT("KZT", "坚戈", CurrencyEnum.KZT.getCode()),
    LAK("LAK", "老挝基普", CurrencyEnum.LAK.getCode()),
    LBP("LBP", "黎巴嫩镑", CurrencyEnum.LBP.getCode()),
    LKR("LKR", "斯里兰卡卢比", CurrencyEnum.LKR.getCode()),
    LRD("LRD", "黎巴嫩元", CurrencyEnum.LRD.getCode()),
    LSL("LSL", "莱索托洛蒂", CurrencyEnum.LSL.getCode()),
    LVL("LVL", "拉脱维亚拉特", CurrencyEnum.LVL.getCode()),
    LYD("LYD", "利比亚第纳尔", CurrencyEnum.LYD.getCode()),
    MAD("MAD", "摩洛哥迪拉姆", CurrencyEnum.MAD.getCode()),
    MBTC("MBTC", "比特币（虚拟货币）", null),
    MYR("MYR", "MYR-马来西亚林吉特-Malaysian Ringgit", CurrencyEnum.MYR.getCode()),
    MDL("MDL", "摩尔多瓦列伊", CurrencyEnum.MDL.getCode()),
    MGA("MGA", "马达加斯加阿里亚里", CurrencyEnum.MGA.getCode()),
    MKD("MKD", "马其顿第纳尔", CurrencyEnum.MKD.getCode()),
    MMK("MMK", "缅甸元", CurrencyEnum.MMK.getCode()),
    MNT("MNT", "蒙古圖格裡克", CurrencyEnum.MNT.getCode()),
    MUR("MUR", "毛里求斯卢比", CurrencyEnum.MUR.getCode()),
    MVR("MVR", "马尔代夫罗非亚", CurrencyEnum.MVR.getCode()),
    MWK("MWK", "马拉维克瓦查", CurrencyEnum.MWK.getCode()),
    MXN("MXN", "墨西哥比索", CurrencyEnum.MXN.getCode()),
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
    PGK("PGK", "巴布亚新几内亚基纳", CurrencyEnum.PGK.getCode()),
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
    SDG("SDG", "苏丹镑", CurrencyEnum.SDG.getCode()),
    SEK("SEK", "瑞典克朗", CurrencyEnum.SEK.getCode()),
    SHP("SHP", "圣赫勒拿磅", CurrencyEnum.SHP.getCode()),
    SLL("SLL", "塞拉利昂利昂", CurrencyEnum.SLL.getCode()),
    SOS("SOS", "索马里先令", CurrencyEnum.SOS.getCode()),
    SRD("SRD", "苏里南元", CurrencyEnum.SRD.getCode()),
    SVC("SVC", "萨尔瓦多科朗", CurrencyEnum.SVC.getCode()),
    SYP("SYP", "叙利亚镑", CurrencyEnum.SYP.getCode()),
    SZL("SZL", "斯威士兰里兰吉尼", CurrencyEnum.SZL.getCode()),
    THB("THB", "泰铢", CurrencyEnum.THB.getCode()),
    TJS("TJS", "塔吉克斯坦索莫尼", CurrencyEnum.TJS.getCode()),
    TMM("TMM", "土库曼斯坦马纳特", CurrencyEnum.TMM.getCode()),
    TND("TND", "突尼斯第纳尔", CurrencyEnum.TND.getCode()),
    TRY("TRY", "新土耳其里拉", CurrencyEnum.TRY.getCode()),
    TTD("TTD", "特立尼达和多巴哥元", CurrencyEnum.TTD.getCode()),
    TWD("TWD", "新台币", CurrencyEnum.TWD.getCode()),
    TZS("TZS", "坦桑尼亚先令", CurrencyEnum.TZS.getCode()),
    UAH("UAH", "乌克兰格里夫纳", CurrencyEnum.UAH.getCode()),
    UGX("UGX", "乌干达先令", CurrencyEnum.UGX.getCode()),
    USD("USD", "美元", CurrencyEnum.USD.getCode()),
    USDT("USDT", "泰达币", CurrencyEnum.USDT.getCode()),
    UYU("UYU", "乌拉圭比索", CurrencyEnum.UYU.getCode()),
    UZS("UZS", "乌兹别克斯坦苏姆", CurrencyEnum.UZS.getCode()),
    VEF("VEF", "委内瑞拉玻利瓦尔", CurrencyEnum.VEF.getCode()),
    VND("KVND", "千越南盾", CurrencyEnum.KVND.getCode()),
    VND1("VND", "越南盾", CurrencyEnum.VND.getCode()),
    VUV("VUV", "瓦努阿图瓦图", CurrencyEnum.VUV.getCode()),
    WST("WST", "萨摩亚塔拉", CurrencyEnum.WST.getCode()),
    XAF("XAF", "中非法郎", CurrencyEnum.XAF.getCode()),
    XCD("XCD", "东加勒比元", CurrencyEnum.XCD.getCode()),
    XDR("XDR", "特别提款权", CurrencyEnum.XDR.getCode()),
    XOF("XOF", "西非法郎", CurrencyEnum.XOF.getCode()),
    XPF("XPF", "太平洋法郎", CurrencyEnum.XPF.getCode()),
    YER("YER", "也门里亚尔", CurrencyEnum.YER.getCode()),
    ZAR("ZAR", "南非兰特", CurrencyEnum.ZAR.getCode()),
    ZMK("ZMK", "赞比亚克瓦查", CurrencyEnum.ZMK.getCode()),
    ZWR("ZWR", "津巴布韦元", CurrencyEnum.ZWR.getCode());




    private final String code;
    private final String name;
    private final String platCurrencyCode;

    PGCurrencyEnum(String code, String name, String platCurrencyCode) {
        this.code = code;
        this.name = name;
        this.platCurrencyCode = platCurrencyCode;
    }

    public static void main(String[] args) {
        PGCurrencyEnum cq9CurrencyEnum = byPlatCurrencyCode("KVND");
        System.out.println(cq9CurrencyEnum);
    }
    /**
     * 根据平台币币种，匹配
     */
    public static PGCurrencyEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (PGCurrencyEnum tmp : PGCurrencyEnum.values()) {
            if (StringUtils.equals(platCurrencyCode,tmp.getPlatCurrencyCode())) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 根据CQ9币种，匹配
     */
    public static PGCurrencyEnum getCurrencyEnum(String currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        PGCurrencyEnum[] types = PGCurrencyEnum.values();
        for (PGCurrencyEnum type : types) {
            if (currencyCode.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
    /*
    * 根据指定1：1000
     */
    public static Boolean isRate1000(String currencyCode) {
        if(currencyCode.equals(PGCurrencyEnum.KRW.getCode())
                || currencyCode.equals(PGCurrencyEnum.IDR.getCode())
                || currencyCode.equals(PGCurrencyEnum.MMK.getCode()) ){
            return true;
        }
        return false;
    }

}
