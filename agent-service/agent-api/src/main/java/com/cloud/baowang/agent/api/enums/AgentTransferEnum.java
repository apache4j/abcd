package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

/**
 * @Author : 小智
 * @Date : 23/10/23 10:27 AM
 * @Version : 1.0
 */
@Getter
public enum AgentTransferEnum {

    QUOTA("2", "额度转账"),
    COMMISSION("1", "佣金转账"),
    ;

    private final String code;

    private final String name;

    AgentTransferEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentTransferEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentTransferEnum[] types = AgentTransferEnum.values();
        for (AgentTransferEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

}
