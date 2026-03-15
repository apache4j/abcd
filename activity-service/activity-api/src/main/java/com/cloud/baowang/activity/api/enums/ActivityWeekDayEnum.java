package com.cloud.baowang.activity.api.enums;


/**
 * 指定日期
 *
 * system_param 中的  week_day
 */
public enum ActivityWeekDayEnum {

    MONDAY(1, "星期一"),
    TUESDAY(3, "星期二"),
    WEDNESDAY(3, "星期三"),
    THURSDAY(4, "星期四"),
    FRIDAY(5, "星期五"),
    SATURDAY(6, "星期六"),
    SUNDAY(7, "星期日")
    ;

    private final Integer code;
    private final String name;

    // 构造方法
    ActivityWeekDayEnum(Integer code, String name) {
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
    public static ActivityWeekDayEnum fromCode(int code) {
        for (ActivityWeekDayEnum type : ActivityWeekDayEnum.values()) {
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
