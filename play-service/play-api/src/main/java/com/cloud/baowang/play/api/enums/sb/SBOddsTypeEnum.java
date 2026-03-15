package com.cloud.baowang.play.api.enums.sb;

public enum SBOddsTypeEnum {

    SPECIAL_HANDICAP(0, "special Odds","特殊盘口"),
    MALAY_ODDS(1, "Malay Odds", "马来盘"),
    CHINA_ODDS(2, "China Odds", "中国盘"),
    DECIMAL_ODDS(3, "Decimal Odds", "欧洲盘"),
    INDO_ODDS(4, "Indo Odds", "印度尼西亚盘"),
    AMERICAN_ODDS(5, "American Odds", "美国盘"),
    MYANMAR_ODDS(6, "Myanmar Odds", "缅甸盘");

    private final int code;
    private final String name;
    private final String description;

    SBOddsTypeEnum(int code, String name) {
        this(code, name, "");
    }

    SBOddsTypeEnum(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
