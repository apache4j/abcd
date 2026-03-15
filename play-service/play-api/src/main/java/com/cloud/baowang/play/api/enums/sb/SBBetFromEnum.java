package com.cloud.baowang.play.api.enums.sb;

public enum SBBetFromEnum {
    MOBILE_TEXT("m", "手机纯文字版"),
    MOBILE_CLASSIC("l", "手机经典版"),
    NEW_ASIA("x", "新亚洲版"),
    MOBILE_LIGHT("c", "手机轻量版"),
    QUICK_LINK("t", "快速连结"),
    SABA_ESPORTS_DESKTOP("1", "沙巴电竞桌机版"),
    SABA_ESPORTS_MOBILE("2", "沙巴电竞手机版"),
    MOBILE_EUROPE("%", "手机欧洲版"),
    BASKETBALL_QUICK_BET_MOBILE("@", "篮球快速下注(只支持手机轻量版)"),
    SABA_WHITE_LABEL_DESKTOP("[", "桌机下注沙巴白牌中国版"),
    SABA_WHITE_LABEL_MOBILE("]", "手机下注沙巴白牌中国版"),
    MOBILE_HOT_GAME("7", "手机下注发烧游戏"),
    DESKTOP_HOT_GAME("9", "桌机下注发烧游戏"),
    VGAMING_MOBI_DESKTOP(">", "Vgaming Mobi 桌机版"),
    VGAMING_MOBI_MOBILE("~", "Vgaming Mobi 手机版"),
    MOBILE_BEGINNER("}", "手机新手版"),
    ANCHOR_RECOMMENDATION("A000", "主播推单"),
    GALAXY_DESKTOP("W001", "桌机银河版"),
    GALAXY_MOBILE("W002", "手机银河版");

    private final String code;
    private final String description;

    SBBetFromEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
