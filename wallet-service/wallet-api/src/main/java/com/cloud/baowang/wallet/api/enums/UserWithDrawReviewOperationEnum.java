package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员提款审核-审核操作枚举值，同system_param user_withdraw_review_operation code
 */
@Getter
@AllArgsConstructor
public enum UserWithDrawReviewOperationEnum {

    PENDING_REVIEW(0, "待一审"),
    PENDING_AUDIT(1, "挂单审核"),
    PENDING_PAYMENT(2, "待出款"),
    CHECK(3, "结单查看"),
    ;


    private final Integer code;
    private final String name;

    public static UserWithDrawReviewOperationEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserWithDrawReviewOperationEnum[] types = UserWithDrawReviewOperationEnum.values();
        for (UserWithDrawReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

}
