package com.cloud.baowang.activity.api.enums;

import lombok.Getter;

/**
 * 活动支持终端
 */
@Getter
public enum ActivitySupportTypeEnum {
    PC(0, "pc"),
    H5(1, "h5"),
    ANDROID(2, "android"),
    IOS(3, "ios");

    private final int type;
    private final String value;

    // 构造函数
    ActivitySupportTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    // 获取整数类型
    public int getType() {
        return type;
    }

    // 获取字符串值
    public String getValue() {
        return value;
    }

    // 根据整数值获取枚举
    public static ActivitySupportTypeEnum fromType(int type) {
        for (ActivitySupportTypeEnum t : ActivitySupportTypeEnum.values()) {
            if (t.getType() == type) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }

    // 根据字符串值获取枚举
    public static ActivitySupportTypeEnum fromValue(String value) {
        for (ActivitySupportTypeEnum t : ActivitySupportTypeEnum.values()) {
            if (t.getValue().equals(value)) {
                return t;
            }
        }
        return null;
    }
}
    
