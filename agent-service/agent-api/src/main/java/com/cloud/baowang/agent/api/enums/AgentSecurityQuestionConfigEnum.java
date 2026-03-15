package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

@Getter
public enum AgentSecurityQuestionConfigEnum {

    ENABLE(1, "启用"),
    DISABLE(0, "停用"),
    ;

    private final int code;
    private final String name;

    AgentSecurityQuestionConfigEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentSecurityQuestionConfigEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentSecurityQuestionConfigEnum[] types = AgentSecurityQuestionConfigEnum.values();
        for (AgentSecurityQuestionConfigEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

}
