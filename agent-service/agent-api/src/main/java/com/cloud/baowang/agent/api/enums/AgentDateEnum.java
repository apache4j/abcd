package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AgentDateEnum {

    TODAY(0, "今天"),
    YESTERDAY(-1, "昨天"),
    SEVEN_DAY(-7, "近7天"),
    THIRTY_DAY(-30, "30天内"),
    CUSTOMIZE(9999, "自定义"),
    ;

    private final Integer code;
    private final String name;

    AgentDateEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentDateEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentDateEnum[] types = AgentDateEnum.values();
        for (AgentDateEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentDateEnum> getList() {
        return Arrays.asList(values());
    }
}
