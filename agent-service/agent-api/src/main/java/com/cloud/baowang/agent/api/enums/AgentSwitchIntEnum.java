package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理开关枚举
 */
public enum AgentSwitchIntEnum {

    OPEN(1, "开启"),
    CLOSE(0, "关闭"),
    ;

    private Integer code;
    private String name;

    AgentSwitchIntEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AgentSwitchIntEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentSwitchIntEnum[] types = AgentSwitchIntEnum.values();
        for (AgentSwitchIntEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentSwitchIntEnum> getList() {
        return Arrays.asList(values());
    }
}
