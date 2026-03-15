package com.cloud.baowang.wallet.api.enums;

import com.cloud.baowang.wallet.api.enums.wallet.TransferEnums;
import lombok.Getter;

@Getter
public enum UpdateBalanceStatusEnums {

    SUCCESS(0, "成功"),
    INSUFFICIENT_BALANCE(1, "余额不足"),
    REPEAT_TRANSACTIONS(2, "交易重复"),
    WALLET_NOT_EXIST(3, "支出无钱包信息"),
    AMOUNT_LESS_ZERO(4, "订单账变金额小于0"),
    FAIL(9, "失败");

    private final Integer code;
    private final String description;

    UpdateBalanceStatusEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UpdateBalanceStatusEnums of(Integer code) {
        if (null == code) {
            return null;
        }
        UpdateBalanceStatusEnums[] types = UpdateBalanceStatusEnums.values();
        for (UpdateBalanceStatusEnums type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


}
