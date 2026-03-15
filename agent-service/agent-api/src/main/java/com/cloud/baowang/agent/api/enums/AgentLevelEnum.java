package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 代理级别
 */
@Getter
public enum AgentLevelEnum {

    PARENT_AGENT(1, "总代"),
    ONE_AGENT(2, "一代"),
    TWO_AGENT(3, "二代"),
    THREE_AGENT(4, "三代"),
    ;

    private final Integer code;
    private final String name;

    AgentLevelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentLevelEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentLevelEnum[] types = AgentLevelEnum.values();
        for (AgentLevelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentLevelEnum> getList() {
        return Arrays.asList(values());
    }

    public static String parseName(Integer level) {
        AgentLevelEnum agentLevelEnum = AgentLevelEnum.nameOfCode(level);
        if(agentLevelEnum!=null){
            return agentLevelEnum.getName();
        }
        return "";
    }
}
