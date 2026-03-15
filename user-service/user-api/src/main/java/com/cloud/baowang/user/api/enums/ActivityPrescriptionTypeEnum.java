package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 站点活动配置-用户范围类型枚举
 * system param  activity_prescription_type
 *
 * @author aomiao
 */
@Getter
public enum ActivityPrescriptionTypeEnum {

    LONG_TERM(0, "长期"),
    TIME_LIMITED(1, "限时"),
    ;

    private final Integer code;
    private final String name;

    ActivityPrescriptionTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取对应枚举
     *
     * @param code code值
     * @return 对应枚举
     */
    public static ActivityPrescriptionTypeEnum getEnumsByCode(Integer code) {
        if (null == code) {
            return null;
        }
        ActivityPrescriptionTypeEnum[] types = ActivityPrescriptionTypeEnum.values();
        for (ActivityPrescriptionTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ActivityPrescriptionTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
