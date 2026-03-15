package com.cloud.baowang.user.api.enums;

/**
 * 极光语言编码
 *
 * @see <a href="https://www.engagelab.com/zh_CN/docs/app-push/rest-api/create-push-api">EngageLab 推送 API 文档 多语言码</a>
 */
public enum LanguageCodeEnum {

    ENGLISH("English", "en"), // 英语 - 美国
    ARABIC("Arabic", "ar"), // 阿拉伯语 - 阿拉伯国家
    CHINESE_SIMPLIFIED("Chinese (Simplified)", "zh-Hans"), // 简体中文 - 中国、新加坡
    CHINESE_TRADITIONAL("Chinese (Traditional)", "zh-Hant"), // 繁体中文 - 台湾、香港、澳门
    CZECH("Czech", "cs"), // 捷克语 - 捷克
    DANISH("Danish", "da"), // 丹麦语 - 丹麦
    DUTCH("Dutch", "nl"), // 荷兰语 - 荷兰、比利时
    FRENCH("French", "fr"), // 法语 - 法国、加拿大、比利时、瑞士等
    GERMAN("German", "de"), // 德语 - 德国、奥地利、瑞士等
    HINDI("Hindi", "hi"), // 印地语 - 印度
    ITALIAN("Italian", "it"), // 意大利语 - 意大利
    JAPANESE("Japanese", "ja"), // 日语 - 日本
    KOREAN("Korean", "ko"), // 韩语 - 韩国
    MALAY("Malay", "ms"), // 马来语 - 马来西亚、新加坡
    RUSSIAN("Russian", "ru"), // 俄语 - 俄罗斯
    SPANISH("Spanish", "es"), // 西班牙语 - 西班牙、拉美国家
    THAI("Thai", "th"), // 泰语 - 泰国
    VIETNAMESE("Vietnamese", "vi"), // 越南语 - 越南
    INDONESIAN("Indonesian", "id"), // 印度尼西亚语 - 印尼
    NORWEGIAN("Norwegian", "no"), // 挪威语 - 挪威
    SWEDISH("Swedish", "sv"), // 瑞典语 - 瑞典
    POLISH("Polish", "pl"), // 波兰语 - 波兰
    TURKISH("Turkish", "tr"), // 土耳其语 - 土耳其
    HEBREW("Hebrew", "he"), // 希伯来语 - 以色列
    PORTUGUESE("Portuguese", "pt"), // 葡萄牙语 - 葡萄牙、巴西
    ROMANIAN("Romanian", "ro"), // 罗马尼亚语 - 罗马尼亚
    HUNGARIAN("Hungarian", "hu"), // 匈牙利语 - 匈牙利
    FINNISH("Finnish", "fi"), // 芬兰语 - 芬兰
    GREEK("Greek", "el"); // 希腊语 - 希腊

    private final String languageName;
    private final String languageCode;

    LanguageCodeEnum(String languageName, String languageCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    // 根据语言名称获取对应的语言代码
    public static String getLanguageCodeByName(String name) {
        for (LanguageCodeEnum lang : values()) {
            if (lang.getLanguageName().equalsIgnoreCase(name)) {
                return lang.getLanguageCode();
            }
        }
        throw new IllegalArgumentException("Unknown language name: " + name);
    }

    // 根据语言代码获取对应的语言名称
    public static String getLanguageNameByCode(String code) {
        for (LanguageCodeEnum lang : values()) {
            if (lang.getLanguageCode().equalsIgnoreCase(code)) {
                return lang.getLanguageName();
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }
}
