package com.cloud.baowang.play.game.ftg;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum FTGLangEnum {

    ZH("zh", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    ZH_TW("zh_tw", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    EN("en", LanguageEnum.EN_US.getLang(), "英文"), //
    ES("es", LanguageEnum.EN_US.getLang(), "西班牙文"), //
    PT("pt", LanguageEnum.PT_BR.getLang(), "葡萄牙文"), //
    JA("ja", LanguageEnum.EN_US.getLang(), "日文"), //
    KO("ko", LanguageEnum.KO_KR.getLang(), "韩文"), //
    TH("th", LanguageEnum.EN_US.getLang(), "泰文"), //
    VI("vi", LanguageEnum.VI_VN.getLang(), "越南文"), //
    IN("in", LanguageEnum.EN_US.getLang(), "印尼文");

    //视讯的语言CODE
    private String code;

    //我们自己平台的语言CODE
    private String platform;


    private String name;


    FTGLangEnum(String code, String platform, String name) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }

    public static String conversionLang(String code) {
        if (StringUtils.isBlank(code)) {
            return EN.code;
        }
        for (FTGLangEnum langEnum : FTGLangEnum.values()) {
            if (code.equals(langEnum.getPlatform())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
