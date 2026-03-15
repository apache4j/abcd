package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentTransferAuditStepEnum {

    STATEMENT_VIEW("0", "结单查看"),
    FIRST_REVIEW("1", "一审审核"),

    ;

    private String code;
    private String name;

    AgentTransferAuditStepEnum(String code, String name) {
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

    public static AgentTransferAuditStepEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentTransferAuditStepEnum[] types = AgentTransferAuditStepEnum.values();
        for (AgentTransferAuditStepEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentTransferAuditStepEnum> getList() {
        return Arrays.asList(values());
    }
}
