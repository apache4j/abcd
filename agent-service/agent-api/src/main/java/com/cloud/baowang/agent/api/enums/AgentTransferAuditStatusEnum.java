package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentTransferAuditStatusEnum {

    PENDING("0", "待处理"),
    PROCESSING("1", "处理中"),
    APPROVED("2", "审核通过"),
    REJECTED("3", "审核拒绝"),

    ;

    private String code;
    private String name;

    AgentTransferAuditStatusEnum(String code, String name) {
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

    public static AgentTransferAuditStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentTransferAuditStatusEnum[] types = AgentTransferAuditStatusEnum.values();
        for (AgentTransferAuditStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentTransferAuditStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
