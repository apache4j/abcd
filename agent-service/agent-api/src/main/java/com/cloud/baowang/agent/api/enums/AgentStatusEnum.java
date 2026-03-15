package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理账号状态
 * system_param 中 agent_status
 */
public enum AgentStatusEnum {

    NORMAL("1", "正常"),
    LOGIN_LOCK("2", "登录锁定"),
    DEPOSIT_WITHDRAWAL_LOCK("3", "充提锁定"),
    ;

    private String code;
    private String name;

    AgentStatusEnum(String code, String name) {
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

    public static AgentStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentStatusEnum[] agentStatusEnums = AgentStatusEnum.values();
        for (AgentStatusEnum agentStatusEnum : agentStatusEnums) {
            if (code.equals(agentStatusEnum.getCode())) {
                return agentStatusEnum;
            }
        }
        return null;
    }

    public static List<AgentStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
