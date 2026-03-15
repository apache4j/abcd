package com.cloud.baowang.play.api.enums.order;

import lombok.Getter;

@Getter
public enum ProcessStatusEnum {
    NOT_PROCESS(0, "未处理"),
    PROCESSED(1, "已处理");

    private final int code;
    private final String name;

    ProcessStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ProcessStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ProcessStatusEnum[] types = ProcessStatusEnum.values();
        for (ProcessStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
