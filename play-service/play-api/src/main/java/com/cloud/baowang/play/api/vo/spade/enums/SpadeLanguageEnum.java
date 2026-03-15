package com.cloud.baowang.play.api.vo.spade.enums;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpadeLanguageEnum {

    en_US(LanguageEnum.EN_US.getLang(), "英文"),
    zh_CN(LanguageEnum.ZH_CN.getLang(), "简体中文"),
    th_TH(LanguageEnum.TH_TH.getLang(), "泰文"),
    id_ID(LanguageEnum.ZH_CN.getLang(), "印尼文"),
    vi_VN(LanguageEnum.VI_VN.getLang(), "越南文"),
    ko_KR(LanguageEnum.KO_KR.getLang(), "韩文"),
    jp_JP(LanguageEnum.JA_JP.getLang(), "日文"),
    ru_RU(LanguageEnum.RU_RU.getLang(), "俄文"),
    tr_TR(LanguageEnum.TR_TR.getLang(), "土耳其文"),
    es_ES(LanguageEnum.ES_ES.getLang(), "西班牙文"),
    fr_FR(LanguageEnum.FR_FR.getLang(), "法文"),
    pt_PT(LanguageEnum.PT_BR.getLang(), "葡萄牙语");
//    bn_BN("bn-BN", "孟加拉语");
//    hi_HI("hi-HI", "印度语");

    private final String code;
    private final String desc;

    /**
     * 根据 code 查找枚举
     */
    public static SpadeLanguageEnum fromCode(String code) {

        if (code==null || LanguageEnum.ZH_TW.getLang().equals(code)){
            return SpadeLanguageEnum.zh_CN;
        }
        for (SpadeLanguageEnum lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return SpadeLanguageEnum.en_US;
    }
}

