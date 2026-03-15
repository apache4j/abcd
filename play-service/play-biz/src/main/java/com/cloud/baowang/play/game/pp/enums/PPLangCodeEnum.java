package com.cloud.baowang.play.game.pp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPLangCodeEnum {

    EN("en", "en-US", "英语"),
    RU("ru", "ru-RU", "俄语"),
    NL("nl", "nl-NL", "荷兰语"),
    FR("fr", "fr-FR", "法语"),
    DE("de", "de-DE", "德语"),
    IT("it", "it-IT", "意大利语"),
    ES("es", "es-ES", "西班牙语"),
    DA("da", "da-DK", "丹麦语"),
    KO("ko", "ko-KR", "韩语"),
    TR("tr", "tr-TR", "土耳其语"),
    JA("ja", "ja-JP", "日语"),
    VI("vi", "vi-VN", "越南语"),
    TH("th", "th-TH", "泰语"),
    RO("ro", "ro-RO", "罗马尼亚语"),
    ZT("zt", "zh-TW", "繁体中文"),
    PL("pl", "pl-PL", "波兰语"),
    BG("bg", "bg-BG", "保加利亚语"),
    LT("lt", "lt-LT", "立陶宛语"),
    LV("lv", "lv-LV", "拉脱维亚语"),
    ZH("zh", "zh-CN", "简体中文"),
    SV("sv", "sv-SE", "瑞典语"),
    FI("fi", "fi-FI", "芬兰语"),
    NO("no", "no-NO", "挪威语"),
    ID("id", "id-ID", "印尼语"),
    SR("sr", "sr-RS", "塞尔维亚语"),
    CS("cs", "cs-CZ", "捷克语"),
    SK("sk", "sk-SK", "斯洛伐克语"),
    ET("et", "et-EE", "爱沙尼亚语"),
    EL("el", "el-GR", "希腊语"),
    HU("hu", "hu-HU", "匈牙利语"),
    PT("pt", "pt-PT", "葡萄牙语"),
    PT_BR("pt", "pt-BR", "葡萄牙语-巴西"),
    HI("hi", "hi-IN", "印地语"),

    MS("ms", "ms-MY", "马来语");

    ;

    private final String code;

    private final String platCode;

    private final String name;


    public static PPLangCodeEnum fromCode(String code) {
        for (PPLangCodeEnum lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }

    public static PPLangCodeEnum fromPlatCode(String platCode) {
        for (PPLangCodeEnum lang : values()) {
            if (lang.platCode.equalsIgnoreCase(platCode)) {
                return lang;
            }
        }
        return PPLangCodeEnum.EN;
    }


}
