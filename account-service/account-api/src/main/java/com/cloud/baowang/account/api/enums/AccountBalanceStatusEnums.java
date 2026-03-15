package com.cloud.baowang.account.api.enums;

import lombok.Getter;

/**
 * @Author ford
 * @Date 2025-10-14
 */
@Getter
public enum AccountBalanceStatusEnums {

    SUCCESS(0, "成功"),
    INSUFFICIENT_BALANCE(1, "余额不足"),
    REPEAT_TRANSACTIONS(2, "交易重复"),
    WALLET_NOT_EXIST(3, "支出无钱包信息"),
    AMOUNT_LESS_ZERO(4, "订单账变金额小于0"),
    FAIL(9, "失败");

    private final Integer code;
    private final String description;

    AccountBalanceStatusEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
