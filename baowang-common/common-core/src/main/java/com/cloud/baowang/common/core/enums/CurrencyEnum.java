package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 货币代码、名称及符号枚举
 */
@Getter
public enum CurrencyEnum {
    USDT("USDT", "泰达币-Tether", "₮"),
    GBP("GBP", "英镑", "£"),
    CHE("CHE", "欧元 (WIR)", "€"),
    THB("THB", "泰铢", "฿"),
    USD("USD", "美元", "$"),
    CNY("CNY", "人民币", "¥"),
    INR("INR", "印度卢比", "₹"),
    IDR("IDR", "印度尼西亚盾", "Rp"),
    BRL("BRL", "巴西雷亚尔", "R$"),
    KVND("KVND", "越南盾K", "₫"),
    VND("VND", "越南盾", "₫"),
    PHP("PHP", "菲律宾比索", "₱"),
    XFO("XFO", "XFO-法国金法郎-French Gold Franc", "₣"),
    XFU("XFU", "XFU-法国法郎 (UIC)-French UIC-Franc", "₣"),
    AFA("AFA", "AFA-阿富汗尼 (1927–2002)-Afghan Afghani (1927–2002)", "؋"),
    ALL("ALL", "ALL-阿尔巴尼亚列克-Albanian Lek", "L"),
    DZD("DZD", "DZD-阿尔及利亚第纳尔-Algerian Dinar", "دج"),
    ADP("ADP", "ADP-安道尔比塞塔-Andorran Peseta", "ADP"),
    AZM("AZM", "AZM-阿塞拜疆马纳特 (1993–2006)-Azerbaijani Manat (1993–2006)", "₼"),
    ARS("ARS", "ARS-阿根廷比索-Argentine Peso", "$"),
    AUD("AUD", "AUD-澳大利亚元-Australian Dollar", "$"),
    ATS("ATS", "ATS-奥地利先令-Austrian Schilling", "S"),
    BSD("BSD", "BSD-巴哈马元-Bahamian Dollar", "$"),
    BHD("BHD", "BHD-巴林第纳尔-Bahraini Dinar", "BHD"),
    BDT("BDT", "BDT-孟加拉塔卡-Bangladeshi Taka", "Tk"),
    AMD("AMD", "AMD-亚美尼亚德拉姆-Armenian Dram", "֏"),
    BBD("BBD", "BBD-巴巴多斯元-Barbadian Dollar", "$"),
    BEF("BEF", "BEF-比利时法郎-Belgian Franc", "BEF"),
    BMD("BMD", "BMD-百慕大元-Bermudan Dollar", "$"),
    BTN("BTN", "BTN-不丹努尔特鲁姆-Bhutanese Ngultrum", "Nu"),
    BOB("BOB", "BOB-玻利维亚诺-Bolivian Boliviano", "Bs"),
    BWP("BWP", "BWP-博茨瓦纳普拉-Botswanan Pula", "P"),
    BZD("BZD", "BZD-伯利兹元-Belize Dollar", "BZ$"),
    SBD("SBD", "SBD-所罗门群岛元-Solomon Islands Dollar", "SI$"),
    BND("BND", "BND-文莱元-Brunei Dollar", "B$"),
    BGL("BGL", "BGL-保加利亚硬列弗-Bulgarian Hard Lev", "BGL"),
    MMK("MMK", "MMK-缅甸元-Myanmar Kyat", "MMK"),
    BIF("BIF", "BIF-布隆迪法郎-Burundian Franc", "FBu"),
    BYB("BYB", "BYB-白俄罗斯新卢布 (1994–1999)-Belarusian Ruble (1994–1999)", "Br"),
    KHR("KHR", "KHR-柬埔寨瑞尔-Cambodian Riel", "៛"),
    CAD("CAD", "CAD-加拿大元-Canadian Dollar", "$"),
    CVE("CVE", "CVE-佛得角埃斯库多-Cape Verdean Escudo", "CVE"),
    KYD("KYD", "KYD-开曼元-Cayman Islands Dollar", "KYD"),
    LKR("LKR", "LKR-斯里兰卡卢比-Sri Lankan Rupee", "₨"),
    CLP("CLP", "CLP-智利比索-Chilean Peso", "$"),
    COP("COP", "COP-哥伦比亚比索-Colombian Peso", "$"),
    KMF("KMF", "KMF-科摩罗法郎-Comorian Franc", "CF"),
    CRC("CRC", "CRC-哥斯达黎加科朗-Costa Rican Colón", "₡"),
    HRK("HRK", "HRK-克罗地亚库纳-Croatian Kuna", "kn"),
    CUP("CUP", "CUP-古巴比索-Cuban Peso", "₱"),
    CYP("CYP", "CYP-塞浦路斯镑-Cypriot Pound", "CYP"),
    CZK("CZK", "CZK-捷克克朗-Czech Koruna", "Kč"),
    DKK("DKK", "DKK-丹麦克朗-Danish Krone", "kr"),
    DOP("DOP", "DOP-多米尼加比索-Dominican Peso", "$"),
    SVC("SVC", "SVC-萨尔瓦多科朗-Salvadoran Colón", "₡"),
    ETB("ETB", "ETB-埃塞俄比亚比尔-Ethiopian Birr", "Br"),
    ERN("ERN", "ERN-厄立特里亚纳克法-Eritrean Nakfa", "Nfk"),
    EEK("EEK", "EEK-爱沙尼亚克朗-Estonian Kroon", "kr"),
    FKP("FKP", "FKP-福克兰群岛镑-Falkland Islands Pound", "£"),
    FJD("FJD", "FJD-斐济元-Fijian Dollar", "FJD"),
    FIM("FIM", "FIM-芬兰马克-Finnish Markka", "mk"),
    FRF("FRF", "FRF-法国法郎-French Franc", "₣"),
    DJF("DJF", "DJF-吉布提法郎-Djiboutian Franc", "Fdj"),
    GMD("GMD", "GMD-冈比亚达拉西-Gambian Dalasi", "D"),
    DEM("DEM", "DEM-德国马克-German Mark", "DM"),
    GHC("GHC", "GHC-加纳塞第-Ghanaian Cedi (1979–2007)", "GH₵"),
    GIP("GIP", "GIP-直布罗陀镑-Gibraltar Pound", "£"),
    GRD("GRD", "GRD-希腊德拉克马-Greek Drachma", "GRD"),
    GTQ("GTQ", "GTQ-危地马拉格查尔-Guatemalan Quetzal", "Q"),
    GNF("GNF", "GNF-几内亚法郎-Guinean Franc", "FG"),
    GYD("GYD", "GYD-圭亚那元-Guyanaese Dollar", "$"),
    HTG("HTG", "HTG-海地古德-Haitian Gourde", "HTG"),
    HNL("HNL", "HNL-洪都拉斯伦皮拉-Honduran Lempira", "L"),
    HKD("HKD", "HKD-港元-Hong Kong Dollar", "HK$"),
    HUF("HUF", "HUF-匈牙利福林-Hungarian Forint", "Ft"),
    ISK("ISK", "ISK-冰岛克朗-Icelandic Króna", "kr"),
    IRR("IRR", "IRR-伊朗里亚尔-Iranian Rial", "IRR"),
    IQD("IQD", "IQD-伊拉克第纳尔-Iraqi Dinar", "IQD"),
    IEP("IEP", "IEP-爱尔兰镑-Irish Pound", "IEP"),
    ILS("ILS", "ILS-以色列新谢克尔-Israeli New Shekel", "₪"),
    ITL("ITL", "ITL-意大利里拉-Italian Lira", "ITL"),
    JMD("JMD", "JMD-牙买加元-Jamaican Dollar", "J$"),
    JPY("JPY", "JPY-日元-Japanese Yen", "JP¥"),
    KZT("KZT", "KZT-哈萨克斯坦坚戈-Kazakhstani Tenge", "₸"),
    JOD("JOD", "JOD-约旦第纳尔-Jordanian Dinar", "JD"),
    KES("KES", "KES-肯尼亚先令-Kenyan Shilling", "KSh"),
    KPW("KPW", "KPW-朝鲜元-North Korean Won", "₩"),
    KRW("KRW", "KRW-韩元-South Korean Won", "￦"),
    KWD("KWD", "KWD-科威特第纳尔-Kuwaiti Dinar", "KD"),
    KGS("KGS", "KGS-吉尔吉斯斯坦索姆-Kyrgystani Som", "лв"),
    LAK("LAK", "LAK-老挝基普-Laotian Kip", "₭"),
    LBP("LBP", "LBP-黎巴嫩镑-Lebanese Pound", "£"),
    LSL("LSL", "LSL-莱索托洛蒂-Lesotho Loti", "LSL"),
    LVL("LVL", "LVL-拉脱维亚拉特-Latvian Lats", "LVL"),
    LRD("LRD", "LRD-利比里亚元-Liberian Dollar", "L$"),
    LYD("LYD", "LYD-利比亚第纳尔-Libyan Dinar", "LD"),
    LTL("LTL", "LTL-立陶宛立特-Lithuanian Litas", "Lt"),
    LUF("LUF", "LUF-卢森堡法郎-Luxembourgian Franc", "LUF"),
    MOP("MOP", "MOP-澳门币-Macanese Pataca", "MOP$"),
    MGF("MGF", "MGF-马达加斯加法郎-Malagasy Franc", "MGF"),
    MWK("MWK", "MWK-马拉维克瓦查-Malawian Kwacha", "MK"),
    MYR("MYR", "MYR-马来西亚林吉特-Malaysian Ringgit", "RM"),
    MVR("MVR", "MVR-马尔代夫卢菲亚-Maldivian Rufiyaa", "Rf"),
    MTL("MTL", "MTL-马耳他里拉-Maltese Lira", "₤"),
    MRO("MRO", "MRO-毛里塔尼亚乌吉亚 (1973–2017)-Mauritanian Ouguiya (1973–2017)", "UM"),
    MUR("MUR", "MUR-毛里求斯卢比-Mauritian Rupee", "MUR"),
    MXN("MXN", "MXN-墨西哥比索-Mexican Peso", "MX$"),
    MNT("MNT", "MNT-蒙古图格里克-Mongolian Tugrik", "₮"),
    MDL("MDL", "MDL-摩尔多瓦列伊-Moldovan Leu", "L"),
    MAD("MAD", "MAD-摩洛哥迪拉姆-Moroccan Dirham", "dh"),
    MZM("MZM", "MZM-旧莫桑比克美提卡-Mozambican Metical (1980–2006)", "MT"),
    OMR("OMR", "OMR-阿曼里亚尔-Omani Rial", "OMR"),
    NAD("NAD", "NAD-纳米比元亚-Namibian Dollar", "N$"),
    NPR("NPR", "NPR-尼泊尔卢比-Nepalese Rupee", "₨"),
    NLG("NLG", "NLG-荷兰盾-Dutch Guilder", "ƒ"),
    ANG("ANG", "ANG-荷属安的列斯盾-Netherlands Antillean Guilder", "ƒ"),
    AWG("AWG", "AWG-阿鲁巴弗罗林-Aruban Florin", "ƒ"),
    VUV("VUV", "VUV-瓦努阿图瓦图-Vanuatu Vatu", "VT"),
    NZD("NZD", "NZD-新西兰元-New Zealand Dollar", "NZ$"),
    NIO("NIO", "NIO-尼加拉瓜科多巴-Nicaraguan Córdoba", "C$"),
    NGN("NGN", "NGN-尼日利亚奈拉-Nigerian Naira", "₦"),
    NOK("NOK", "NOK-挪威克朗-Norwegian Krone", "kr"),
    PKR("PKR", "PKR-巴基斯坦卢比-Pakistani Rupee", "₨"),
    PAB("PAB", "PAB-巴拿马巴波亚-Panamanian Balboa", "PAB"),
    PGK("PGK", "PGK-巴布亚新几内亚基那-Papua New Guinean Kina", "K"),
    PYG("PYG", "PYG-巴拉圭瓜拉尼-Paraguayan Guarani", "Gs"),
    PEN("PEN", "PEN-秘鲁索尔-Peruvian Sol", "S/."),
    PTE("PTE", "PTE-葡萄牙埃斯库多-Portuguese Escudo", "$"),
    GWP("GWP", "GWP-几内亚比绍比索-Guinea-Bissau Peso", "GWP"),
    TPE("TPE", "TPE-帝汶埃斯库多-Timorese Escudo", "£"),
    QAR("QAR", "QAR-卡塔尔里亚尔-Qatari Rial", "QAR"),
    ROL("ROL", "ROL-旧罗马尼亚列伊-Romanian Leu (1952–2006)", "ROL"),
    RUB("RUB", "RUB-俄罗斯卢布-Russian Ruble", "₽"),
    RWF("RWF", "RWF-卢旺达法郎-Rwandan Franc", "R₣"),
    SHP("SHP", "SHP-圣赫勒拿群岛磅-St. Helena Pound", "£"),
    STD("STD", "STD-圣多美和普林西比多布拉 (1977–2017)-São Tomé & Príncipe Dobra (1977–2017)", "Db"),
    SAR("SAR", "SAR-沙特里亚尔-Saudi Riyal", "SAR"),
    SCR("SCR", "SCR-塞舌尔卢比-Seychellois Rupee", "₨"),
    SLL("SLL", "SLL-塞拉利昂利昂-Sierra Leonean Leone", "Le"),
    SGD("SGD", "SGD-新加坡元-Singapore Dollar", "S$"),
    SKK("SKK", "SKK-斯洛伐克克朗-Slovak Koruna", "SKK"),
    SIT("SIT", "SIT-斯洛文尼亚托拉尔-Slovenian Tolar", "SIT"),
    SOS("SOS", "SOS-索马里先令-Somali Shilling", "S"),
    ZAR("ZAR", "ZAR-南非兰特-South African Rand", "R"),
    ZWD("ZWD", "ZWD-津巴布韦元 (1980–2008)-Zimbabwean Dollar (1980–2008)", "Z$"),
    ESP("ESP", "ESP-西班牙比塞塔-Spanish Peseta", "ESP"),
    SSP("SSP", "SSP-南苏丹镑-South Sudanese Pound", "SD"),
    SDD("SDD", "SDD-苏丹第纳尔 (1992–2007)-Sudanese Dinar (1992–2007)", "SDD"),
    SRG("SRG", "SRG-苏里南盾-Surinamese Guilder", "$"),
    SZL("SZL", "SZL-斯威士兰里兰吉尼-Swazi Lilangeni", "E"),
    SEK("SEK", "SEK-瑞典克朗-Swedish Krona", "kr"),
    CHF("CHF", "CHF-瑞士法郎-Swiss Franc", "CHF"),
    SYP("SYP", "SYP-叙利亚镑-Syrian Pound", "£"),
    TOP("TOP", "TOP-汤加潘加-Tongan Paʻanga", "T$"),
    TTD("TTD", "TTD-特立尼达和多巴哥元-Trinidad & Tobago Dollar", "TTD"),
    AED("AED", "AED-阿联酋迪拉姆-United Arab Emirates Dirham", "د.إ"),
    TND("TND", "TND-突尼斯第纳尔-Tunisian Dinar", "DT"),
    TRL("TRL", "TRL-土耳其里拉 (1922–2005)-Turkish Lira (1922–2005)", "TL"),
    TMM("TMM", "TMM-土库曼斯坦马纳特 (1993–2009)-Turkmenistani Manat (1993–2009)", "T"),
    UGX("UGX", "UGX-乌干达先令-Ugandan Shilling", "USh"),
    MKD("MKD", "MKD-马其顿第纳尔-Macedonian Denar", "ден"),
    RUR("RUR", "RUR-俄国卢布 (1991–1998)-Russian Ruble (1991–1998)", "₽"),
    EGP("EGP", "EGP-埃及镑-Egyptian Pound", "£"),
    TZS("TZS", "TZS-坦桑尼亚先令-Tanzanian Shilling", "TSh"),
    UYU("UYU", "UYU-乌拉圭比索-Uruguayan Peso", "UYU"),
    UZS("UZS", "UZS-乌兹别克斯坦苏姆-Uzbekistani Som", "лв"),
    VEB("VEB", "VEB-委内瑞拉玻利瓦尔 (1871–2008)-Venezuelan Bolívar (1871–2008)", "Bs.S"),
    WST("WST", "WST-萨摩亚塔拉-Samoan Tala", "WS$"),
    YER("YER", "YER-也门里亚尔-Yemeni Rial", "﷼"),
    CSD("CSD", "CSD-旧塞尔维亚第纳尔-Serbian Dinar (2002–2006)", "CSD"),
    YUM("YUM", "YUM-南斯拉夫新第纳尔 (1994–2002)-Yugoslavian New Dinar (1994–2002)", "YUM"),
    ZMK("ZMK", "ZMK-赞比亚克瓦查 (1968–2012)-Zambian Kwacha (1968–2012)", "ZK"),
    TWD("TWD", "TWD-新台币-New Taiwan Dollar", "NT$"),
    SLE("SLE", "SLE-Sierra Leonean Leone-Sierra Leonean Leone", "SLE"),
    VED("VED", "VED-Venezuelan Bolívar Soberano-Venezuelan Bolívar Soberano", "VED"),
    VES("VES", "VES-委内瑞拉玻利瓦尔-Venezuelan Bolívar", "Bs.S"),
    MRU("MRU", "MRU-毛里塔尼亚乌吉亚-Mauritanian Ouguiya", "UM"),
    STN("STN", "STN-圣多美和普林西比多布拉-São Tomé & Príncipe Dobra", "Db"),
    CUC("CUC", "CUC-古巴可兑换比索-Cuban Convertible Peso", "CUC"),
    ZWL("ZWL", "ZWL-津巴布韦元 (2009)-Zimbabwean Dollar (2009)", "Z$"),
    BYN("BYN", "BYN-白俄罗斯卢布-Belarusian Ruble", "Br"),
    TMT("TMT", "TMT-土库曼斯坦马纳特-Turkmenistani Manat", "T"),
    ZWR("ZWR", "ZWR-津巴布韦元 (2008)-Zimbabwean Dollar (2008)", "Z$"),
    GHS("GHS", "GHS-加纳塞地-Ghanaian Cedi", "GH₵"),
    VEF("VEF", "VEF-委内瑞拉玻利瓦尔 (2008–2018)-Venezuelan Bolívar (2008–2018)", "Bs"),
    SDG("SDG", "SDG-苏丹镑-Sudanese Pound", "SDG"),
    UYI("UYI", "UYI-乌拉圭比索（索引单位）-Uruguayan Peso (Indexed Units)", "$U"),
    RSD("RSD", "RSD-塞尔维亚第纳尔-Serbian Dinar", "Дин"),
    ZWN("ZWN", "ZWN-ZWN-ZWN", "ZWN"),
    MZN("MZN", "MZN-莫桑比克美提卡-Mozambican Metical", "MT"),
    AZN("AZN", "AZN-阿塞拜疆马纳特-Azerbaijani Manat", "₼"),
    AYM("AYM", "AYM-AYM-AYM", "AYM"),
    RON("RON", "RON-罗马尼亚列伊-Romanian Leu", "lei"),
    CHW("CHW", "CHW-法郎 (WIR)-WIR Franc", "CHW"),
    TRY("TRY", "TRY-土耳其里拉-Turkish Lira", "₺"),
    XAF("XAF", "XAF-中非法郎-Central African CFA Franc", "FCFA"),
    XCD("XCD", "XCD-东加勒比元-East Caribbean Dollar", "$"),
    XCG("XCG", "XCG-加勒比盾-Caribbean guilder", "ƒ"),
    XOF("XOF", "XOF-西非法郎-West African CFA Franc", "CFA"),
    XPF("XPF", "XPF-太平洋法郎-CFP Franc", "₣"),
    XBA("XBA", "XBA-欧洲复合单位-European Composite Unit", "XBA"),
    XBB("XBB", "XBB-欧洲货币联盟-European Monetary Unit", "XBB"),
    XBC("XBC", "XBC-欧洲计算单位 (XBC)-European Unit of Account (XBC)", "XBC"),
    XBD("XBD", "XBD-欧洲计算单位 (XBD)-European Unit of Account (XBD)", "XBD"),
    XAU("XAU", "XAU-黄金-Gold", "XAU"),
    XDR("XDR", "XDR-特别提款权-Special Drawing Rights", "XDR"),
    XAG("XAG", "XAG-银-Silver", "XAG"),
    XPT("XPT", "XPT-铂-Platinum", "XPT"),
    XTS("XTS", "XTS-测试货币代码-Testing Currency Code", "XTS"),
    XPD("XPD", "XPD-钯-Palladium", "XPD"),
    XUA("XUA", "XUA-非洲开发银行记账单位-ADB Unit of Account", "XUA"),
    ZMW("ZMW", "ZMW-赞比亚克瓦查-Zambian Kwacha", "ZK"),
    SRD("SRD", "SRD-苏里南元-Surinamese Dollar", "$"),
    MGA("MGA", "MGA-马达加斯加阿里亚里-Malagasy Ariary", "Ar"),
    COU("COU", "COU-哥伦比亚币-Colombian Real Value Unit", "COU"),
    AFN("AFN", "AFN-阿富汗尼-Afghan Afghani", "؋"),
    TJS("TJS", "TJS-塔吉克斯坦索莫尼-Tajikistani Somoni", "SM"),
    AOA("AOA", "AOA-安哥拉宽扎-Angolan Kwanza", "Kz"),
    BYR("BYR", "BYR-白俄罗斯卢布 (2000–2016)-Belarusian Ruble (2000–2016)", "BYR"),
    BGN("BGN", "BGN-保加利亚列弗-Bulgarian Lev", "лв"),
    CDF("CDF", "CDF-刚果法郎-Congolese Franc", "FC"),
    BAM("BAM", "BAM-波斯尼亚-黑塞哥维那可兑换马克-Bosnia-Herzegovina Convertible Mark", "BAM"),
    EUR("EUR", "EUR-欧元-Euro", "€"),
    MXV("MXV", "MXV-墨西哥（资金）-Mexican Investment Unit", "MXV"),
    UAH("UAH", "UAH-乌克兰格里夫纳-Ukrainian Hryvnia", "₴"),
    GEL("GEL", "GEL-格鲁吉亚拉里-Georgian Lari", "₾"),
    BOV("BOV", "BOV-玻利维亚 Mvdol（资金）-Bolivian Mvdol", "BOV"),
    PLN("PLN", "PLN-波兰兹罗提-Polish Zloty", "zł"),
    CLF("CLF", "CLF-智利（资金）-Chilean Unit of Account (UF)", "CLF"),
    XSU("XSU", "XSU-苏克雷-Sucre", "XSU"),
    USN("USN", "USN-美元（次日）-US Dollar (Next day)", "USN"),
    USS("USS", "USS-美元（当日）-US Dollar (Same day)", "USS"),
    XXX("XXX", "XXX-未知货币-Unknown Currency", "XXX");


    private String code;
    private String name;
    private final String symbol;

    CurrencyEnum(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static boolean isUSD(String currency) {
        return USD.getCode().equals(currency);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CurrencyEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        CurrencyEnum[] types = CurrencyEnum.values();
        for (CurrencyEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            CurrencyEnum[] types = CurrencyEnum.values();
            for (CurrencyEnum type : types) {
                System.out.print(type+",");
            }
            System.out.println();

        }

    }
    public static String symbolByCode(String code) {
        CurrencyEnum currencyEnum = nameOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getSymbol();
    }

    public static String nameByCode(String code) {
        CurrencyEnum currencyEnum = nameOfCode(code);
        if (code == null) {
            return null;
        }
        return currencyEnum.getName();
    }


    public static List<CurrencyEnum> getList() {
        return Arrays.asList(values());
    }

}
