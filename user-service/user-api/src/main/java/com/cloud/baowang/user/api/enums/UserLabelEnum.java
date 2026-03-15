package com.cloud.baowang.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户标签，定制化7种类型，与数据库id一致
 */
@AllArgsConstructor
@Getter
public enum UserLabelEnum {

    WITHDRAWAL_NO_REQUIREMENTS("100001", "提款免流水"),
    NO_DEPOSIT_BONUS("100002", "无存款优惠"),
    NO_RANK_BONUS("100003", "无等级优惠"),
    WITHDRAWAL_LIMIT("100004", "限制出款"),
    NO_CASHBACK("100005", "不返水"),
    NO_PARTICIPATION_ACTIVITY("100006", "不参加活动"),
    MONEY_LAUNDERING_ACCOUNT("100007", "洗钱账户");

    private final String labelId;
    private final String name;


}
