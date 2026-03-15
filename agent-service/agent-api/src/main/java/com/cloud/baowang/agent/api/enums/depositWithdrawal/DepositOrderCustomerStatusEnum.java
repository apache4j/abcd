package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

/**
 * DEPOSIT_ORDER_CUSTOMER_STATUS
 * system_param deposit_order_customer_status
 */
public enum DepositOrderCustomerStatusEnum {

    PENDING("0", "处理中"),
    SUCCESS("1", "存款成功"),
    FAIL("2", "存款失败"),
    ;
    private String code;
    private String name;

    DepositOrderCustomerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DepositOrderCustomerStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DepositOrderCustomerStatusEnum[] types = DepositOrderCustomerStatusEnum.values();
        for (DepositOrderCustomerStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<DepositOrderCustomerStatusEnum> getList() {
        return Arrays.asList(values());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
