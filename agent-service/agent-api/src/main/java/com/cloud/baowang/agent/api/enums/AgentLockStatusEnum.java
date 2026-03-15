package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理锁单状态 枚举类
 */
public enum AgentLockStatusEnum {

    LOCK(1, "已锁"),
    UNLOCK(0, "未锁"),
            ;

    private Integer code;
    private String name;

    AgentLockStatusEnum(Integer code, String name) {
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

    public static AgentLockStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentLockStatusEnum[] types = AgentLockStatusEnum.values();
        for (AgentLockStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentLockStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
