package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentTypeEnum {

    FORMAL("1", "正式"),
    TEST("2", "测试"),
    COOPERATE("3", "合作"),
    ;

    private String code;
    private String name;

    AgentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
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

    public static AgentTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentTypeEnum[] types = AgentTypeEnum.values();
        for (AgentTypeEnum type : types) {
            if (code.toString().equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public static String parseName(Integer code) {
        if (null == code) {
            return null;
        }
        AgentTypeEnum[] types = AgentTypeEnum.values();
        for (AgentTypeEnum type : types) {
            if (code.toString().equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }
}
