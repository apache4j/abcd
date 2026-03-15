package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

public enum AgentWithdrawalOrderCustomerStatusEnum {

    PENDING("0", "处理中"),
    SUCCESS("1", "取款成功"),
    FAIL("2", "取款失败"),
    ;
    private String code;
    private String name;

    AgentWithdrawalOrderCustomerStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentWithdrawalOrderCustomerStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentWithdrawalOrderCustomerStatusEnum[] types = AgentWithdrawalOrderCustomerStatusEnum.values();
        for (AgentWithdrawalOrderCustomerStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentWithdrawalOrderCustomerStatusEnum> getList() {
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
