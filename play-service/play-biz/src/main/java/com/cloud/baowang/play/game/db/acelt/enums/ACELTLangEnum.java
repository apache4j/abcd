package com.cloud.baowang.play.game.db.acelt.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ACELTLangEnum {

    CN("1", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    ZH_TW("2", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    EN("3", LanguageEnum.EN_US.getLang(), "英文"), //
    TH("6", LanguageEnum.TH_TH.getLang(), "泰文"), //
    VN("4", LanguageEnum.VI_VN.getLang(), "越南文"), //
    KOR("10", LanguageEnum.KO_KR.getLang(), "韩文"),
    ID("7", LanguageEnum.ID_ID.getLang(), "印尼语"),
    JPN("8", LanguageEnum.PT_BR.getLang(), "葡萄牙语");
    private final String code;
    private final String language;
    private final String name;

    ACELTLangEnum(String code, String language, String name) {
        this.code = code;
        this.language = language;
        this.name = name;
    }

    public static String conversionLang(String language) {
        if (StringUtils.isBlank(language)) {
            return EN.code;
        }
        for (ACELTLangEnum langEnum : ACELTLangEnum.values()) {
            if (language.equals(langEnum.getLanguage())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
