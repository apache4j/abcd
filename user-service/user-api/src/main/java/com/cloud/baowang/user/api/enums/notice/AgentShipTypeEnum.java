package com.cloud.baowang.user.api.enums.notice;

import lombok.Getter;

/**
 *  会员类型 1:全部会员 2:特定会员 agent_type
 */
@Getter
public enum AgentShipTypeEnum {

    ALL_AGENS(1, "全部代理"),
    SPECIFIC_AGENTS(2, "特定代理"),
    ;



    private final int code;
    private final String name;

    AgentShipTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentShipTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentShipTypeEnum[] types = AgentShipTypeEnum.values();
        for (AgentShipTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
