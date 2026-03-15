package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 站点活动配置-用户范围类型枚举
 *
 * @author aomiao
 */
@Getter
public enum ActivityUserRangeEnum {

    ALL_USER(0, "全部会员"),
    USER_RANK(1, "会员段位"),
    USER_LABEL(2, "会员标签"),
    ;

    private final Integer code;
    private final String name;

    ActivityUserRangeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取对应枚举
     *
     * @param code code值
     * @return 对应枚举
     */
    public static ActivityUserRangeEnum getEnumsByCode(Integer code) {
        if (null == code) {
            return null;
        }
        ActivityUserRangeEnum[] types = ActivityUserRangeEnum.values();
        for (ActivityUserRangeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ActivityUserRangeEnum> getList() {
        return Arrays.asList(values());
    }

}
