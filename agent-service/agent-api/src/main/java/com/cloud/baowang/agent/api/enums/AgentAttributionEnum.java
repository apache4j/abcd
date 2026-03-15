package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理归属 1推广 2招商 3官资
 */
public enum AgentAttributionEnum {

    POPULARIZE(1, "推广"),
    INVESTMENT_PROMOTION(2, "招商"),
    OFFICIAL_CAPITAL(3, "官资"),
    ;

    private Integer code;
    private String name;

    AgentAttributionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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

    public static AgentAttributionEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentAttributionEnum[] types = AgentAttributionEnum.values();
        for (AgentAttributionEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentAttributionEnum> getList() {
        return Arrays.asList(values());
    }
}
