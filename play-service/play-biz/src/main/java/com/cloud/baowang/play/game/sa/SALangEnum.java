package com.cloud.baowang.play.game.sa;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum SALangEnum {

    AR_001("ar-001", "阿拉伯语", LanguageEnum.AR_SA.getLang()),
    BN("bn", "孟加拉语", LanguageEnum.BN_BD.getLang()),
    EN_US("en-us", "英语", LanguageEnum.EN_US.getLang()),
    ES("es", "西班牙语", LanguageEnum.ES_ES.getLang()),
    HI("hi", "印地语", LanguageEnum.HI_IN.getLang()),
    ID("id", "印尼语", LanguageEnum.ID_ID.getLang()),
    JA("ja", "日语", LanguageEnum.JA_JP.getLang()),
    KO("ko", "韩语", LanguageEnum.KO_KR.getLang()),
    MS("ms", "马来语", LanguageEnum.MS_MY.getLang()),
    PT("pt", "葡萄牙语", LanguageEnum.PT_BR.getLang()),
    PT_BR("pt-br", "巴西葡萄牙语", LanguageEnum.PT_BR.getLang()),
    RU("ru", "俄语", LanguageEnum.RU_RU.getLang()),
    TH("th", "泰语", LanguageEnum.TH_TH.getLang()),
    VI("vi", "越南语", LanguageEnum.VI_VN.getLang()),
    ZH_HANS("zh-hans", "简体中文", LanguageEnum.ZH_CN.getLang()),
    ZH_HANT("zh-hant", "繁体中文", LanguageEnum.ZH_TW.getLang());

    private final String code;
    private final String name;
    private final String platform;

    SALangEnum(String code, String name, String platform) {
        this.code = code;
        this.platform = platform;
        this.name = name;
    }


    public static SALangEnum byPlatCurrencyCode(String platCurrencyCode) {
        for (SALangEnum tmp : SALangEnum.values()) {
            if (tmp.getPlatform().equals(platCurrencyCode)) {
                return tmp;
            }
        }
        return SALangEnum.EN_US;
    }


}
