package com.cloud.baowang.play.game.winto.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum WintoEVGLangEnum {

    CN("cn", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    ZH_TW("tw", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    EN("en", LanguageEnum.EN_US.getLang(), "英文"), //
    TH("th", LanguageEnum.EN_US.getLang(), "泰文"), //
    VN("vi", LanguageEnum.VI_VN.getLang(), "越南文"), //
    ID("id", LanguageEnum.EN_US.getLang(), "印尼文"),
    PT("pt", LanguageEnum.PT_BR.getLang(), "葡萄牙文"), //
    SPA("spa", LanguageEnum.EN_US.getLang(), "西班牙文"),
    KOR("kor", LanguageEnum.KO_KR.getLang(), "韩文"),
    RUS("rus", LanguageEnum.EN_US.getLang(), "俄罗斯文"),
    JPN("jpn", LanguageEnum.EN_US.getLang(), "日文");
    private String code;
    private String platform;
    private String name;

    WintoEVGLangEnum(String code, String platform, String name) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }

    public static String conversionLang(String platform) {
        if (StringUtils.isBlank(platform)) {
            return EN.code;
        }
        for (WintoEVGLangEnum langEnum : WintoEVGLangEnum.values()) {
            if (platform.equals(langEnum.getPlatform())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
