package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AgentClientDeviceEnum {

    APP(1, "APP"),
    PC(2, "PC"),
    H5(3, "H5"),
    ;

    private Integer code;
    private String name;

    AgentClientDeviceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentClientDeviceEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentClientDeviceEnum[] types = AgentClientDeviceEnum.values();
        for (AgentClientDeviceEnum type : types) {
            if (code == type.getCode()) {
                return type;
            }
        }
        return null;
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

    public static List<AgentClientDeviceEnum> getList() {
        return Arrays.asList(values());
    }
}
