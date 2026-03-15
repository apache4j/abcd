package com.cloud.baowang.agent.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum AgentCoinBalanceTypeEnum {


    INCOME("1", "收入"),
    EXPENSES("2", "支出"),
    FREEZE("3", "冻结"),
    UN_FREEZE("4", "解冻"),
    ;

    private String code;
    private String name;

    AgentCoinBalanceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentCoinBalanceTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentCoinBalanceTypeEnum[] types = AgentCoinBalanceTypeEnum.values();
        for (AgentCoinBalanceTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentCoinBalanceTypeEnum> getList() {
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
