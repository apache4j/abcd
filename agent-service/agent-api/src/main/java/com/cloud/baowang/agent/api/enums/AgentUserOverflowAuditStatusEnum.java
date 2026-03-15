package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentUserOverflowAuditStatusEnum {

    PENDING("0", "待审核"),
    PROCESSING("1", "审核中"),
    APPROVED("2", "审核通过"),
    REJECTED("3", "审核拒绝"),

    ;

    private String code;
    private String name;

    AgentUserOverflowAuditStatusEnum(String code, String name) {
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

    public static AgentUserOverflowAuditStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentUserOverflowAuditStatusEnum[] types = AgentUserOverflowAuditStatusEnum.values();
        for (AgentUserOverflowAuditStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentUserOverflowAuditStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
