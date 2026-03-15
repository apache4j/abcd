package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * 审核状态-二审
 * system_param user_manual_order_status code
 */
@AllArgsConstructor
@Getter
public enum UserManualOrderStatusEnum {
    WAIT_ONE_REVIEW(1, "待一审"),
    ONE_REVIEWING(2, "一审审核"),
    ONE_REVIEW_FAIL(3, "一审拒绝"),
    WAIT_TWO_REVIEW(4, "待二审"),
    TWO_REVIEWING(5, "二审审核"),
    TWO_REVIEW_FAIL(6, "二审拒绝"),
    REVIEW_SUCCESS(7, "审核通过"),
    ;

    private final Integer code;
    private final String name;

    public static UserManualOrderStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserManualOrderStatusEnum[] types = UserManualOrderStatusEnum.values();
        for (UserManualOrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserManualOrderStatusEnum> getList() {
        return Arrays.asList(values());
    }

}
