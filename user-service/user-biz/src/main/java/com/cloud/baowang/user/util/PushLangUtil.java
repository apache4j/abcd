package com.cloud.baowang.user.util;

import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.user.api.enums.LanguageCodeEnum;

/**
 * @className: PushLangUtils
 * @author: wade
 * @description: LanguageEnum
 * @date: 19/11/24 14:50
 */
public class PushLangUtil {

    /**
     * 从包网的语言code，转换为极光的语言code
     *
     * @param langCode
     * @return
     */
    public static String getLangCode(String langCode) {
        if (LanguageEnum.ZH_CN.getLang().equals(langCode)) {
            return LanguageCodeEnum.CHINESE_SIMPLIFIED.getLanguageCode();
        } else if (LanguageEnum.EN_US.getLang().equals(langCode)) {
            return LanguageCodeEnum.ENGLISH.getLanguageCode();
        } else if (LanguageEnum.PT_BR.getLang().equals(langCode)) {
            return LanguageCodeEnum.PORTUGUESE.getLanguageCode();
        } else if (LanguageEnum.VI_VN.getLang().equals(langCode)) {
            return LanguageCodeEnum.VIETNAMESE.getLanguageCode();
        } else {
            return LanguageCodeEnum.ENGLISH.getLanguageCode();
        }
    }


}
