package com.cloud.baowang.play.api.enums.sb;

import com.cloud.baowang.common.core.enums.LanguageEnum;

public enum SBLanguageEnum {

    CS("cs", LanguageEnum.ZH_CN.getLang(), "中文-简体"),
    ch("ch", LanguageEnum.ZH_TW.getLang(), "中文-繁体"),
    EN("en", LanguageEnum.EN_US.getLang(), "英语-美国"),
    PT("cs", LanguageEnum.EN_US.getLang(), "葡萄牙语-巴西"),
    VN("vn", LanguageEnum.VI_VN.getLang(), "越南语"),
    KO("ko", LanguageEnum.KO_KR.getLang(), "韩文"),
    JP("jp", LanguageEnum.JA_JP.getLang(), "日文"),
    TH("th", LanguageEnum.TH_TH.getLang(), "泰文"),
    ID("id", LanguageEnum.ID_ID.getLang(), "印度尼西亚文"),
    ;



    private final String sbaLang;
    private final String platLangCode;
    private final String desc;

    public String getSbaLang() {
        return sbaLang;
    }

    public String getPlatLangCode() {
        return platLangCode;
    }

    public String getDesc() {
        return desc;
    }

    SBLanguageEnum(String sbaLang, String platLangCode, String desc) {
        this.sbaLang = sbaLang;
        this.platLangCode = platLangCode;
        this.desc = desc;
    }

    /**
     * 默认语言
     *
     * @return lang
     */
    public static String getDefaultLang() {
        return SBLanguageEnum.EN.sbaLang;
    }

    public static String getPlatLangCodeForm(String countryCode) {
        if (null == countryCode) {
            return getDefaultLang();
        }
        SBLanguageEnum[] types = SBLanguageEnum.values();
        for (SBLanguageEnum type : types) {
            if (countryCode.equals(type.getPlatLangCode())) {
                return type.sbaLang;
            }
        }
        return getDefaultLang();
    }

    public static String parseNameByCode(String langCode) {
        return LanguageEnum.parseNameByCode(langCode);
    }

    public static String getCodeByLang(String language) {
        return LanguageEnum.getCodeByLang(language);
    }
}
