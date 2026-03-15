package com.cloud.baowang.websocket.api.enums;

import lombok.Getter;

@Getter
public enum SendTypeEnum {

    PART(1, "部分"),
    ALL(2, "全部");

    private final int code;
    private final String name;

    SendTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
