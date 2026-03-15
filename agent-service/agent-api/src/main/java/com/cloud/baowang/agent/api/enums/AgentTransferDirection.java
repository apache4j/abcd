package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

@Getter
public enum AgentTransferDirection {

    SUPER_AGENT("0", "上级转入"),
    UNDER_AGENT("1", "转给下级"),
    ;

    private final String code;

    private final String name;

    AgentTransferDirection(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentTransferDirection nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentTransferDirection[] types = AgentTransferDirection.values();
        for (AgentTransferDirection type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
