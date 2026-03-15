package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同system_param user_withdraw_review_status code值
 */
@Getter
@AllArgsConstructor
public enum UserWithdrawReviewStatusEnum {
    PENDING_REVIEW(0, "待审核"),
    IN_PROGRESS(1, "进行中"),
    FIRST_REVIEW_REJECTED(2, "一审拒绝"),
    PENDING_SUSPEND_REVIEW(3, "待挂起审核"),
    SUSPEND_REVIEW_REJECTED(4, "挂起审核拒绝"),
    REVIEW_REJECTED(5, "审核拒绝"),
    REVIEW_APPROVED(6, "审核通过");

    private final int code;
    private final String name;
}
