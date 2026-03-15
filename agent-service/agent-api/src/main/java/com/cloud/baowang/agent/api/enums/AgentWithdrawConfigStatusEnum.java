package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AgentWithdrawConfigStatusEnum {

    OPEN(1, "开启"),
    CLOSE(0, "关闭"),
    DELETE(-1, "删除");

    private Integer code;
    private String name;

    AgentWithdrawConfigStatusEnum(Integer code, String name) {
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

    public static AgentWithdrawConfigStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentWithdrawConfigStatusEnum[] types = AgentWithdrawConfigStatusEnum.values();
        for (AgentWithdrawConfigStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentWithdrawConfigStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
