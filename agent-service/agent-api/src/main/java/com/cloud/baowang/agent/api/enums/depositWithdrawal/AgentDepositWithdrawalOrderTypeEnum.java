package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

/**
 * 代理存款取款订单类型
 *
 * @author qiqi
 */

public enum AgentDepositWithdrawalOrderTypeEnum {
    DEPOSIT(1, "存款"),
    WITHDRAWAL(2, "取款"),
    ;
    private Integer code;
    private String name;

    AgentDepositWithdrawalOrderTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentDepositWithdrawalOrderTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentDepositWithdrawalOrderTypeEnum[] types = AgentDepositWithdrawalOrderTypeEnum.values();
        for (AgentDepositWithdrawalOrderTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentDepositWithdrawalOrderTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
