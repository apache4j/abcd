package com.cloud.baowang.play.game.nextSpin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NextSpinLangCodeEnum {

    BN_BD("bn-BD", "bn_BD", "孟加拉语"),
    DE_DE("de-DE","de_DE", "德文"),
    EN_US("en-US","en_US", "英文"),
    ES_ES("es-ES","es_ES", "西班牙语"),
    FIL_PH("fil-PH", "fil_PH", "菲律宾语"),
    HI_IN("hi-IN","hi_IN", "印地语"),
    ID_ID("id-ID","id_ID", "印度尼西亚语"),
    JA_JP("ja-JP","ja_JP", "日语"),
    KH_KM("kh-KM", "kh_KM", "高棉语"),
    KO_KR("ko-KR", "ko_KR", "韩语"),
    MY_MM("my-MM", "my_MM", "缅甸语"),
    PT_PT("pt-BR","pt_PT", "葡萄牙语"),
    SV_SE("sv-SE","sv_SE", "瑞典语"),
    TH_TH("th-TH","th_TH", "泰语"),
    VI_VN("vi-VN","vi_VN", "越南语"),
    ZH_CN("zh-CN","zh_CN", "简体中文"),
    ZH_TW("zh-TW","zh_TW", "繁体中文");

    private final String systemLangCode;
    private final String code;
    private final String name;

    public static NextSpinLangCodeEnum fromCode(String systemLangCode) {
        for (NextSpinLangCodeEnum lang : values()) {
            if (lang.systemLangCode.equalsIgnoreCase(systemLangCode)) {
                return lang;
            }
        }
        return NextSpinLangCodeEnum.EN_US;
    }
}
