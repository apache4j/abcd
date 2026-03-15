package com.cloud.baowang.play.game.db.sh.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum DBSHLangEnum {

    CN("zh", LanguageEnum.ZH_CN.getLang(), "简体中文"), //
    ZH_TW("tw", LanguageEnum.ZH_TW.getLang(), "繁体中文"), //
    EN("en", LanguageEnum.EN_US.getLang(), "英文"), //
    TH("th", LanguageEnum.EN_US.getLang(), "泰文"), //
    VN("vi", LanguageEnum.VI_VN.getLang(), "越南文"), //
    JPN("jp", LanguageEnum.KO_KR.getLang(), "日文"),
    KR("kr", LanguageEnum.EN_US.getLang(), "韩文"),
  ;
    private String code;
    private String platform;
    private String name;

    DBSHLangEnum(String code, String platform, String name) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }

    public static String conversionLang(String platform) {
        if (StringUtils.isBlank(platform)) {
            return EN.code;
        }
        for (DBSHLangEnum langEnum : DBSHLangEnum.values()) {
            if (platform.equals(langEnum.getPlatform())) {
                return langEnum.getCode();
            }
        }
        return EN.code;
    }


}
