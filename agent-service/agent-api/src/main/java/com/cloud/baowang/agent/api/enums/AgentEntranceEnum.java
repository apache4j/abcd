package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AgentEntranceEnum {

    OPEN("1", "开启"),
    CLOSE("0", "关闭"),
    ;

    private final String code;
    private final String name;

    AgentEntranceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentEntranceEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentEntranceEnum[] types = AgentEntranceEnum.values();
        for (AgentEntranceEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentEntranceEnum> getList() {
        return Arrays.asList(values());
    }
}
