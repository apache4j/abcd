package com.cloud.baowang.common.core.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;


@Getter
public enum LanguageEnum {

    ZH_CN("zh-CN", "CN", "简体中文",true),
    ZH_TW("zh-TW", "TW", "繁体中文",true),
    EN_US("en-US", "US", "英语-美国",true),
    PT_BR("pt-BR", "BR", "葡萄牙语-巴西",true),
    VI_VN("vi-VN", "VN", "越南语",true),
    KO_KR("ko-KR", "KR", "韩语-韩国",true),
    HI_IN("hi-IN", "IN", "印地语-印度",true),
    ZH_SG("zh-SG", "SG", "简体中文-新加坡",false),
    FR_FR("fr-FR", "FR", "法语-法国",false),
    DE_DE("de-DE", "DE", "德语-德国",false),
    ES_ES("es-ES", "ES", "西班牙语-西班牙",false),
    IT_IT("it-IT", "IT", "意大利语-意大利",false),
    RU_RU("ru-RU", "RU", "俄语-俄罗斯",false),
    AR_SA("ar-SA", "SA", "阿拉伯语-沙特阿拉伯",false),
    TH_TH("th-TH", "TH", "泰语-泰国",false),
    MS_MY("ms-MY", "MY", "马来语-马来西亚",false),
    ID_ID("id-ID", "ID", "印尼语",false),
    TR_TR("tr-TR", "TR", "土耳其语",false),
    NL_NL("nl-NL", "NL", "荷兰语-荷兰",false),
    NL_BE("nl-BE", "BE", "荷兰语-比利时",false),
    SV_SE("sv-SE", "SE", "瑞典语",false),
    FI_FI("fi-FI", "FI", "芬兰语",false),
    DA_DK("da-DK", "DK", "丹麦语",false),
    NO_NO("no-NO", "NO", "挪威语",false),
    PL_PL("pl-PL", "PL", "波兰语",false),
    CS_CZ("cs-CZ", "CZ", "捷克语",false),
    HU_HU("hu-HU", "HU", "匈牙利语",false),
    RO_RO("ro-RO", "RO", "罗马尼亚语",false),
    EL_GR("el-GR", "GR", "希腊语",false),
    HE_IL("he-IL", "IL", "希伯来语",false),
    UK_UA("uk-UA", "UA", "乌克兰语",false),
    JA_JP("ja-JP", "JP", "日语",false),
    BN_BD("bn-BD", "BD", "孟加拉语",false),
    FIL_PH("fil-PH", "PH", "菲律宾语",false),
    KH_KM("kh-KM", "KM", "高棉语",false),

;
    private final String lang;
    private final String countryCode;
    private final String desc;
    //系统是否支持本地化翻译 data-transfer项目里的Resource
    private final boolean localTransLateFlag;

    LanguageEnum(String lang, String countryCode, String desc,boolean localTransLateFlag) {
        this.lang = lang;
        this.countryCode = countryCode;
        this.desc = desc;
        this.localTransLateFlag=localTransLateFlag;
    }

    /**
     * 默认语言
     *
     * @return lang
     */
    public static String getDefaultLang() {
        return EN_US.lang;
    }

    public static String getLangByCountryCode(String countryCode) {
        if (null == countryCode) {
            return getDefaultLang();
        }
        LanguageEnum[] types = LanguageEnum.values();
        for (LanguageEnum type : types) {
            if (countryCode.equals(type.getCountryCode())) {
                return type.getLang();
            }
        }
        return getDefaultLang();
    }

    public static String parseNameByCode(String langCode) {
        if (!StringUtils.hasText(langCode)) {
            return null;
        }
        LanguageEnum[] types = LanguageEnum.values();
        for (LanguageEnum languageEnum : types) {
            if (langCode.equals(languageEnum.getLang())) {
                return languageEnum.getDesc();
            }
        }
        return null;
    }

    public static String getCodeByLang(String language) {
        if (null == language) {
            return null;
        }
        LanguageEnum[] types = LanguageEnum.values();
        for (LanguageEnum type : types) {
            if (language.equals(type.getLang())) {
                return type.getCountryCode();
            }
        }
        return null;
    }


    public static List<LanguageEnum> getList() {
        return Arrays.asList(values());
    }
}
