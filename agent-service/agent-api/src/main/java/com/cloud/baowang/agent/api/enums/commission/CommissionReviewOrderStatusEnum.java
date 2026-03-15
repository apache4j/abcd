package com.cloud.baowang.agent.api.enums.commission;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 审核状态-二审
 * system_param user_manual_order_status code
 */
@AllArgsConstructor
@Getter
public enum CommissionReviewOrderStatusEnum {
    /**
     * WAIT_REVIEW(1, "待审核"),
     * ONE_REVIEWING(2, "审核中"),
     * REVIEW_SUCCESS(3, "通过"),
     * REVIEW_FAIL(4, "拒绝"),  以前的通过等于现在二审通过;拒绝等于一审拒绝
     */
    PENDING_FIRST_REVIEW(1,"待一审"),
    FIRST_REVIEW_IN_PROGRESS(2, "一审审核"),
    REVIEW_SUCCESS(3,"审核通过"),
    FIRST_REVIEW_REJECTED(4, "一审拒绝"),
    FIRST_REVIEW_APPROVED(5, "一审成功"),
    PENDING_SECOND_REVIEW(6, "待二审"),
    SECOND_REVIEW_IN_PROGRESS(7,"二审审核"),
    SECOND_REVIEW_REJECTED(8,"二审拒绝"),
    SECOND_REVIEW_RETURNED(9,"二审驳回")        //驳回只用于了构建备注

    ;

    private final Integer code;
    private final String name;

    public static CommissionReviewOrderStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        CommissionReviewOrderStatusEnum[] types = CommissionReviewOrderStatusEnum.values();
        for (CommissionReviewOrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<CommissionReviewOrderStatusEnum> getList() {
        return Arrays.asList(values());
    }

}
