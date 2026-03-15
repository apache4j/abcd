package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentUserOverflowAuditStepEnum {

    STATEMENT_VIEW("0", "结单查看"),
    FIRST_REVIEW("1", "一审审核"),

    ;

    private String code;
    private String name;

    AgentUserOverflowAuditStepEnum(String code, String name) {
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

    public static AgentUserOverflowAuditStepEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentUserOverflowAuditStepEnum[] types = AgentUserOverflowAuditStepEnum.values();
        for (AgentUserOverflowAuditStepEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentUserOverflowAuditStepEnum> getList() {
        return Arrays.asList(values());
    }
}
