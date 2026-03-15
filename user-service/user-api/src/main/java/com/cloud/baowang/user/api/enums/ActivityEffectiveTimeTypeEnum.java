package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 站点活动配置-红包雨-活动有效时间类型
 * system param  activity_prescription_type
 *
 * @author aomiao
 */
@Getter
public enum ActivityEffectiveTimeTypeEnum {

    EVERY_DAY(0, "每日"),
    APPOINT_TIME(1, "指定时间段"),
    ;

    private final Integer code;
    private final String name;

    ActivityEffectiveTimeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取对应枚举
     *
     * @param code code值
     * @return 对应枚举
     */
    public static ActivityEffectiveTimeTypeEnum getEnumsByCode(Integer code) {
        if (null == code) {
            return null;
        }
        ActivityEffectiveTimeTypeEnum[] types = ActivityEffectiveTimeTypeEnum.values();
        for (ActivityEffectiveTimeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ActivityEffectiveTimeTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
