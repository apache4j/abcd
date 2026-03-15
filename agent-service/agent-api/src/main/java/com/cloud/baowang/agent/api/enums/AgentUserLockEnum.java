package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentUserLockEnum {

    UNLOCK("0", "未锁定"),
    LOCKED("1", "已锁定"),

    ;

    private String code;
    private String name;

    AgentUserLockEnum(String code, String name) {
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

    public static AgentUserLockEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentUserLockEnum[] types = AgentUserLockEnum.values();
        for (AgentUserLockEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentUserLockEnum> getList() {
        return Arrays.asList(values());
    }
}
