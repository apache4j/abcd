package com.cloud.baowang.activity.api.enums;

public enum ReleaseTimeTypeEnum {

    DISTRIBUTION_TIME(0, "发放时间"),
    COLLECTION_TIME(1, "领取时间");

    private final Integer type;
    private final String description;

    ReleaseTimeTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // 根据 type 查询枚举的方法
    public static ReleaseTimeTypeEnum fromType(Integer type) {
        for (ReleaseTimeTypeEnum value : ReleaseTimeTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
