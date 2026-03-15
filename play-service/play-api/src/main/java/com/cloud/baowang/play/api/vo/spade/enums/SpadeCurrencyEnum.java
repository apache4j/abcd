package com.cloud.baowang.play.api.vo.spade.enums;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpadeCurrencyEnum {

    UST(CurrencyEnum.USDT.getCode(), "USDT", "泰达币-Tether"),

    BDT("BDT", "Bangladeshi Taka", "孟加拉塔卡"),
    MXN("MXN", "Mexican Peso", "墨西哥比索"),
    PHP(CurrencyEnum.PHP.getCode(), "Philippine Peso", "菲律宾比索"),
    CNY(CurrencyEnum.CNY.getCode(), "Chinese Yuan (Renminbi)", "人民币"),
    NZD("NZD", "New Zealand Dollar", "纽西兰元"),
    USD(CurrencyEnum.USD.getCode(), "US Dollar", "美元"),
    PEN("PEN", "Peruvian New Sol", "秘鲁新索尔"),
    EUR("EUR", "Euro", "欧元"),
    PLN("PLN", "Polish Zloty", "波兰兹罗提"),
    EPV("EPV", "Euro", "欧元"),
    PYG("PYG", "Paraguayan Guarani", "巴拉圭瓜拉尼"),
    GBP("GBP", "British Pound", "英镑"),
    RSD("RSD", "Serbian Dinar", "塞尔维亚第纳尔"),
    HKD("HKD", "Hong Kong Dollar", "港币"),
    UYU("UYU", "Uruguayan Peso", "乌拉圭比索"),
    KRW(CurrencyEnum.KRW.getCode(), "South Korean Won", "韩元"),
    ZAR("ZAR", "South African Rand", "南非兰特"),
    JPY("JPY", "Japanese Yen", "日元"),
    UAH("UAH", "Ukrainian hryvnia", "乌克兰格里夫纳"),
    THB("THB", "Thai Baht", "泰铢"),
    TND("TND", "Tunisian Dinar", "突尼西亚第纳尔"),
    IDR("IDR", "Indonesia Rupiah", "印尼盾"),
    RUB("RUB", "Russian ruble", "俄罗斯卢布"),
    ID2("ID2", "Indonesia Rupiah", "印尼盾"),
    TRY("TRY", "Turkish Lira", "土耳其里拉"),
    INR(CurrencyEnum.INR.getCode(), "Indian Rupee", "印度卢比"),
    SEK("SEK", "Swedish Krona", "瑞典克朗"),

    VN2("VND", "Viet Nam Dong", "越南盾"),
    NOK("NOK", "Norwegian Krone", "挪威克朗"),
    VND(CurrencyEnum.KVND.getCode(), "Viet Nam Dong", "越南盾"),

//    VND("VND", "Viet Nam Dong", "越南盾"),
//    NOK("NOK", "Norwegian Krone", "挪威克朗"),
//    VN2(CurrencyEnum.KVND.getCode(), "Viet Nam Dong", "越南盾"),

    AED("AED", "Dirham", "迪拉姆"),
    MYR(CurrencyEnum.MYR.getCode(), "Malaysia Ringgit", "马来西亚零吉"),
    AMD("AMD", "Armenian Dram", "亚美尼亚德拉姆"),
    MMK("MMK", "Myanmar Kyat", "缅元"),
    CAD("CAD", "Canadian Dollar", "加拿大元"),
    MMV("MMV", "Myanmar Kyat", "缅元"),
    CHF("CHF", "Swiss franc", "瑞士法郎"),
    AUD("AUD", "Australian Dollar", "澳大利亚元"),
    CLP("CLP", "Chilean Peso", "智利披索"),
    ALL("ALL", "Albania", "阿尔巴尼亚"),
    CZK("CZK", "Czech Koruna", "捷克克朗"),
    BRL(CurrencyEnum.BRL.getCode(), "Brazilian Real", "巴西雷亚尔"),
    DKK("DKK", "Danish Krone", "丹麦克朗"),
    BGN("BGN", "Bulgarian Lev", "保加利亞列弗"),
    ARS("ARS", "Argentine Peso", "阿根廷比索"),
    IRR("IRR", "伊朗里亚尔", "伊朗里亚尔"),
    IR2("IR2", "伊朗里亚尔", "伊朗里亚尔"),
    KZT("KZT", "Kazakhstani Tenge", "哈萨克斯坦坚戈"),
    XAF("XAF", "Central African CFA franc", "中非法郎"),
    AZN("AZN", "Azerbaijan Manat", "新马纳特"),
    ILS("ILS", "Israeli Shekel", "以色列新谢克尔"),
    COP("COP", "Colombian Peso", "哥伦比亚的比索"),
    CO2("CO2", "Colombian Peso", "哥伦比亚的比索"),
    PKR(CurrencyEnum.PKR.getCode(), "Pakistan Rupee", "巴基斯坦卢比"),
    MDL("MDL", "Moldovan leu", "摩尔多瓦列伊"),
    XOF("XOF", "West African CFA franc", "西非法郎"),
    NGN("NGN", "Nigerian Nairas", "尼日利亚奈拉"),
    LBP("LBP", "Lebanese pound", "黎巴嫩镑"),
    TJS("TJS", "Tajikistani somoni", "塔吉克斯坦索莫"),
    ETB("ETB", "Ethiopian birr", "埃塞俄比亚比尔"),
    KGS("KGS", "Kyrgyzstani som", "吉尔吉斯斯坦索姆"),
    BAM("BAM", "Bosnia-Herzegovina Convertible Marka", "马克"),
    BND("BND", "Bruneian Dollars", "汶萊元"),
    TMT("TMT", "Turkmenistani manat", "土库曼斯坦马纳特"),
    KSH("KSH", "Kenyan shilling", "肯雅先令"),
    DEM("DEM", "Deutsche Mark", "德国马克"),
    BYN("BYN", "Belarusian ruble", "白俄罗斯卢布"),
    RON("RON", "Romanian leu", "罗马尼亚列伊"),
    GEL("GEL", "Georgian lari", "格鲁吉亚拉里"),
    KHR("KHR", "Cambodian Riels", "柬埔寨瑞尔"),
    LAK("LAK", "Lao Kip", "老挝基普"),
    BIF("BIF", "Burundian Francs", "布隆迪法郎"),
    SGD("SGD", "Singapore Dollar", "新加坡元"),

    ;

    private final String code;
    private final String desc;
    private final String descCn;

    public static SpadeCurrencyEnum fromCode(String code) {
        for (SpadeCurrencyEnum c : SpadeCurrencyEnum.values()) {
            if (c.code.equalsIgnoreCase(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}

