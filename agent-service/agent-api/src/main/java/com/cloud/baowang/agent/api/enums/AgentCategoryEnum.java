package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 代理类别 1常规代理 2流量代理
 *
 * system_param 中 agent_category
 */
@Getter
public enum AgentCategoryEnum {

    REGULAR_AGENT(1, "常规代理"),
    FLOW_AGENT(2, "流量代理"),
    ;

    private final Integer code;
    private final String name;

    AgentCategoryEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentCategoryEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentCategoryEnum[] types = AgentCategoryEnum.values();
        for (AgentCategoryEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentCategoryEnum> getList() {
        return Arrays.asList(values());
    }
}
