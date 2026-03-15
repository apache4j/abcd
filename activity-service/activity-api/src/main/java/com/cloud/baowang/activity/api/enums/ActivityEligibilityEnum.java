package com.cloud.baowang.activity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 参与资格
 * system_param中的activity_eligibility
 */
@Getter
@AllArgsConstructor
public enum ActivityEligibilityEnum {
    PHONE(0, "完成手机号绑定才能参与"),
    EMAIL(1, "完成邮箱绑定才能参与"),
    IP(2, "同登录IP只能1次");

    private final int value;
    private final String description;

    // 根据值获取枚举实例
    public static ActivityEligibilityEnum fromValue(int value) {
        for (ActivityEligibilityEnum period : ActivityEligibilityEnum.values()) {
            if (period.getValue() == value) {
                return period;
            }
        }
        return null;
    }
}
