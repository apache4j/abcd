package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 会员溢出审核-设备类型（system_param device_terminal）
 */
@Getter
public enum AgentOverFlowDeviceTypeEnum {

    PC(1, "PC"),
    IOS_H5(2, "IOS_H5"),
    IOS_APP(3, "IOS_APP"),
    Android_H5(4, "Android_H5"),
    Android_APP(5, "Android_APP"),
    ;

    private final Integer code;
    private final String name;

    AgentOverFlowDeviceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentOverFlowDeviceTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentOverFlowDeviceTypeEnum[] types = AgentOverFlowDeviceTypeEnum.values();
        for (AgentOverFlowDeviceTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentOverFlowDeviceTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public static String parseName(Integer level) {
        AgentOverFlowDeviceTypeEnum agentLevelEnum = AgentOverFlowDeviceTypeEnum.nameOfCode(level);
        if (agentLevelEnum != null) {
            return agentLevelEnum.getName();
        }
        return "";
    }
}
