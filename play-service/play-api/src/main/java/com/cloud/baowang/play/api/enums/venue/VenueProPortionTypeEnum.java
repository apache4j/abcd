package com.cloud.baowang.play.api.enums.venue;

public enum VenueProPortionTypeEnum  {
    VENUE_LOSS_RATE(0, "场馆负盈利费率"),
    VENUE_EFFECTIVE_FLOW_RATE(1, "场馆有效流水费率"),
    LOSS_AND_EFFECTIVE_FLOW_RATE(2, "负盈利&有效流水费率");

    private final int code;
    private final String description;

    VenueProPortionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 通过code获取枚举值
    public static VenueProPortionTypeEnum getByCode(Integer code) {
        for (VenueProPortionTypeEnum type : VenueProPortionTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
