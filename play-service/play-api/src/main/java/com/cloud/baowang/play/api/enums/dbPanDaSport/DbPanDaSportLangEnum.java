package com.cloud.baowang.play.api.enums.dbPanDaSport;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum DbPanDaSportLangEnum {
    ZH("zh", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    EN("en", LanguageEnum.EN_US.getLang(), "英文"), //
    VI("vi", LanguageEnum.VI_VN.getLang(), "越南文"), //
    ZH_TW("tw", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    TH("th", LanguageEnum.TH_TH.getLang(), "泰文"), //
    ML("ms", LanguageEnum.MS_MY.getLang(), "马来语"), //
    JA("ad", LanguageEnum.ID_ID.getLang(), "印尼语"), //
    KO("ko", LanguageEnum.KO_KR.getLang(), "韩文"),
    PT("pt", LanguageEnum.PT_BR.getLang(), "葡萄牙语"), //
    ES_ES("es", LanguageEnum.ES_ES.getLang(), "西班牙语"); //

    //视讯的语言CODE
    private String code;

    //我们自己平台的语言CODE
    private String platform;


    private String name;


    DbPanDaSportLangEnum(String code, String platform, String name) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }

    public static String byPlatLang(String code) {
        if (StringUtils.isBlank(code)) {
            return EN.code;
        }
        for (DbPanDaSportLangEnum langEnum : DbPanDaSportLangEnum.values()) {
            if (code.equals(langEnum.getPlatform())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
