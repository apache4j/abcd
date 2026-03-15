package com.cloud.baowang.activity.api.enums;


/**
 * 结算周期
 *
 * system_param 中的  calculate_type
 */
public enum ActivityCalculateTypeEnum {

    DAY(0, "日"),
    WEEK(1, "周"),
    MONTH(2, "月");

    private final Integer code;
    private final String name;

    // 构造方法
    ActivityCalculateTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取 code 的方法
    public Integer getCode() {
        return code;
    }

    // 获取 name 的方法
    public String getName() {
        return name;
    }

    // 通过 code 获取枚举
    public static ActivityCalculateTypeEnum fromCode(int code) {
        for (ActivityCalculateTypeEnum type : ActivityCalculateTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ActivityCalculateTypeEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
