package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param
 */
@Getter
public enum UserChangeAgentReviewStatusEnum {
    REVIEW_PENDING("1", "待审核"),
    /**
     * 锁单后状态变更为处理中
     */
    REVIEW_PROGRESS("2", "处理中"),
    REVIEW_PASS("3", "审核通过"),
    REVIEW_REJECTED("4", "审核驳回");

    private final String code;
    private final String name;

    UserChangeAgentReviewStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code获取对应枚举
     *
     * @param code code值
     * @return 对应枚举
     */
    public static UserChangeAgentReviewStatusEnum getEnumsByCode(String code) {
        if (null == code) {
            return null;
        }
        UserChangeAgentReviewStatusEnum[] types = UserChangeAgentReviewStatusEnum.values();
        for (UserChangeAgentReviewStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserChangeAgentReviewStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
