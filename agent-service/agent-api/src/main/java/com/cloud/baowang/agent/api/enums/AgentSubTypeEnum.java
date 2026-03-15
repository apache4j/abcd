package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AgentSubTypeEnum {

    SUB("1", "直属下级"),
    ALL_SUB("2", "全部下级"),
    ;

    private final String code;
    private final String name;

    AgentSubTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentSubTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentSubTypeEnum[] types = AgentSubTypeEnum.values();
        for (AgentSubTypeEnum type : types) {
            if (code.toString().equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentSubTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
