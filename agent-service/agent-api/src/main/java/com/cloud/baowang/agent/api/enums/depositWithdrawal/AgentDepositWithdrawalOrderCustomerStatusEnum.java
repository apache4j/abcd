package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

public enum AgentDepositWithdrawalOrderCustomerStatusEnum {

    PENDING("0", "处理中"),
    SUCCESS("1", "充值成功"),
    FAIL("2", "充值失败"),
    ;
    private String code;
    private String name;

    AgentDepositWithdrawalOrderCustomerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentDepositWithdrawalOrderCustomerStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentDepositWithdrawalOrderCustomerStatusEnum[] types = AgentDepositWithdrawalOrderCustomerStatusEnum.values();
        for (AgentDepositWithdrawalOrderCustomerStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentDepositWithdrawalOrderCustomerStatusEnum> getList() {
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
