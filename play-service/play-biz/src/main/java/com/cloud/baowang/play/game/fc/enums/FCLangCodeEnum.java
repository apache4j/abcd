package com.cloud.baowang.play.game.fc.enums;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FCLangCodeEnum {


    EN(1, LanguageEnum.EN_US.getLang(),"英文"),
    CN(2,LanguageEnum.ZH_CN.getLang(),"简体中文 (Lucky 9 不支援)"),
    VI(3,LanguageEnum.VI_VN.getLang(),"越南文 (Lucky 9 不支援)"),
    TH(4,LanguageEnum.TH_TH.getLang(),"泰文 (Super Color Game & Lucky 9 不支援)"),
    ID(5,LanguageEnum.ID_ID.getLang(),"印尼文 (Super Color Game & Lucky 9 不支援)"),
    MY(6,LanguageEnum.MS_MY.getLang(),"缅甸文 (Super Color Game & Lucky 9 不支援)"),
    JA(7,LanguageEnum.JA_JP.getLang(),"日文 (Super Color Game & Lucky 9 不支援)"),
    KO(8,LanguageEnum.KO_KR.getLang(),"韩文 (Super Color Game & Lucky 9 不支援)"),
    PT(9,LanguageEnum.PT_BR.getLang(),"葡萄牙文 (Super Color Game & Lucky 9 不支援)"),
    ES(10,LanguageEnum.ES_ES.getLang(),"西班牙文 (Super Color Game & Lucky 9 不支援)"),
    TR(12,LanguageEnum.TR_TR.getLang(),"土耳其文 (Super Color Game & Lucky 9 不支援)"),
    BN(14,LanguageEnum.BN_BD.getLang(),"孟加拉文 (Super Color Game & Lucky 9 不支援)"),;

    private final int num;
    private final String code;
    private final String desc;

    public static FCLangCodeEnum fromCode(String code) {
        for (FCLangCodeEnum lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return EN;
    }
}
