package com.cloud.baowang.play.game.ace.enums;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ACEGameLangEnum {


    ENGLISH(1, LanguageEnum.EN_US.getLang(),"English"),
    MANDARIN(2, LanguageEnum.EN_US.getLang(),"Mandarin"),
    THAI(3,LanguageEnum.EN_US.getLang(), "Thai"),
    INDONESIAN(5,LanguageEnum.EN_US.getLang(), "Indonesia"),
    VIETNAMESE(7,LanguageEnum.EN_US.getLang(), "Vietnam"),


    UNKNOWN(1, LanguageEnum.EN_US.getLang(),"Vietnam"),


    ;

    private final int code;
    private final String lang;
    private final String desc;

    public static ACEGameLangEnum fromCode(String lang) {
        for (ACEGameLangEnum langEnum : values()) {
            if (langEnum.lang.equals(lang) ) {
                return langEnum;
            }
        }
        return UNKNOWN;
    }

}
