package com.cloud.baowang.play.game.acelt.enums;


import com.cloud.baowang.common.core.enums.LanguageEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum AceltLangEnum {

    ZH_CN("zh", LanguageEnum.ZH_CN.getLang(), "中文"),
    EN_US("en", LanguageEnum.EN_US.getLang(), "英语-美国"),
    ZH_TW("zh_tw", LanguageEnum.EN_US.getLang(), "繁体中文"),//彩票没有繁体,暂时转英文试试
    VI_VN("vi", LanguageEnum.VI_VN.getLang(), "越南语");


    //视讯的语言CODE
    private String shCode;

    //我们自己平台的语言CODE
    private String platform;


    private String name;


    AceltLangEnum(String shCode, String platform, String name) {
        this.shCode = shCode;
        this.platform = platform;
        this.name = name;
    }

    /**
     * 目前视讯只有 英文跟中文,没匹配上的统一返英文
     * 传入平台的语言CODE,返回SH的语言CODE
     */
    public static String conversionLang(String code) {
        if (StringUtils.isBlank(code)) {
            return EN_US.shCode;
        }
        for (AceltLangEnum langEnum : AceltLangEnum.values()) {
            if (langEnum.getPlatform().equals(code)) {
                return langEnum.getShCode();
            }
        }
        return EN_US.shCode;
    }


}
