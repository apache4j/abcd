package com.cloud.baowang.play.game.zf.openApi.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ZFLang {
    ZH_CH(LanguageEnum.ZH_CN.getLang(),"zh-CN","中文"),
    EN_US(LanguageEnum.EN_US.getLang(),"en-US",  "英语-美国"),
    PT_BR(LanguageEnum.PT_BR.getLang(),"pt-BR", "葡萄牙语-巴西"),
    ZH_TW(LanguageEnum.ZH_TW.getLang(),"zh-CN", "繁体-中文"),
    VI_VN(LanguageEnum.VI_VN.getLang(),"vi-VN", "越南语"),
    ;

    private final String localLang;
    private final String lang;
    private final String desc;

    public static String getLangByLocalLang(String localLang){
        if(localLang == null){
            return null;
        }
        for (ZFLang value : ZFLang.values()) {
            if (value.getLocalLang().equals(localLang)) {
                return value.getLang();
            }
        }
        return null;
    }


}
