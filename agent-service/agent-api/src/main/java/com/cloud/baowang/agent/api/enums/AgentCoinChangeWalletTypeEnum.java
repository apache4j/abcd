package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentCoinChangeWalletTypeEnum {

    COMMISSION(1, "佣金钱包"),
    DEPOSIT(2, "代存钱包"),
    ;

    private Integer code;
    private String name;

    AgentCoinChangeWalletTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentCoinChangeWalletTypeEnum[] types = AgentCoinChangeWalletTypeEnum.values();
        for (AgentCoinChangeWalletTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
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

    public static List<AgentCoinChangeWalletTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
