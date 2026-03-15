package com.cloud.baowang.play.game.cmd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum CMDLangCodeEnum {

    ZH_CN("zh-CN","zh-CN","简体中文"),
    EN_US("en-US", "en-US", "英文"),
    th_TH("th-TH", "th-TH", "泰文"),
    ZH_TW("zh-TW", "zh-TW","繁体中文"),
    VI_VN("vi-VN", "vi-VN", "越南语"),
    ID_ID("id-ID", "id-ID","印度尼西亚语"),
    PT_PT("pt-BR","pt-PT", "葡萄牙语"),
    ES_ES("es-ES","es-ES", "西班牙语"),
    KO_KR("ko-KR", "ko-KR", "韩语"),
    ;
    private final String systemLangCode;
    private final String code;
    private final String name;

    public static CMDLangCodeEnum fromCode(String systemLangCode) {
        if (StringUtils.isEmpty(systemLangCode)){
            return CMDLangCodeEnum.ZH_CN;
        }
        for (CMDLangCodeEnum lang : values()) {
            if (lang.systemLangCode.equalsIgnoreCase(systemLangCode)) {
                return lang;
            }
        }
        return CMDLangCodeEnum.EN_US;
    }
}
