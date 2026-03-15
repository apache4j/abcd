package com.cloud.baowang.play.game.sexy.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum SEXYLangEnum {

    CN("cn", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    ZH_TW("cn", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    EN("en", LanguageEnum.EN_US.getLang(), "英文"), //
    TH("th", LanguageEnum.EN_US.getLang(), "泰文"), //
    VN("vn", LanguageEnum.VI_VN.getLang(), "越南文"), //
    KOR("kr", LanguageEnum.KO_KR.getLang(), "韩文"),
    JPN("jp", LanguageEnum.EN_US.getLang(), "日文");
    private String code;
    private String platform;
    private String name;

    SEXYLangEnum(String code, String platform, String name) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }

    public static String conversionLang(String platform) {
        if (StringUtils.isBlank(platform)) {
            return EN.code;
        }
        for (SEXYLangEnum langEnum : SEXYLangEnum.values()) {
            if (platform.equals(langEnum.getPlatform())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
