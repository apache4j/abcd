package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 注册方式
 * system_param中的 register_way
 */
@Getter
public enum RegisterWayEnum {

    MANUAL(1, "手动"),
    AUTOMATIC(2, "自动"),
    ;

    private final Integer code;
    private final String name;

    RegisterWayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RegisterWayEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        RegisterWayEnum[] types = RegisterWayEnum.values();
        for (RegisterWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<RegisterWayEnum> getList() {
        return Arrays.asList(values());
    }
}
