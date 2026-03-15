package com.cloud.baowang.wallet.api.enums.usercoin;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param  deposit_withdrawal_order_customer_status
 * {@link com.cloud.baowang.common.core.enums.SystemParamTypeEnum DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS}
 */
@Getter
public enum DepositWithdrawalOrderCustomerStatusEnum {

    PENDING("0", "处理中"),
    SUCCESS("1", "成功"),
    FAIL("2", "失败"),
    ;
    private final String code;
    private final String name;

    DepositWithdrawalOrderCustomerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DepositWithdrawalOrderCustomerStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DepositWithdrawalOrderCustomerStatusEnum[] types = DepositWithdrawalOrderCustomerStatusEnum.values();
        for (DepositWithdrawalOrderCustomerStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<DepositWithdrawalOrderCustomerStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
