package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理开关枚举
 */
public enum AgentSwitchEnum {

    OPEN("1", "开启"),
    CLOSE("0", "关闭"),
    ;

    private String code;
    private String name;

    AgentSwitchEnum(String code, String name) {
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

    public static AgentSwitchEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentSwitchEnum[] types = AgentSwitchEnum.values();
        for (AgentSwitchEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentSwitchEnum> getList() {
        return Arrays.asList(values());
    }
}
