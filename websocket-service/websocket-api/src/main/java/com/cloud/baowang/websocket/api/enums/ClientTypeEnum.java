package com.cloud.baowang.websocket.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ClientTypeEnum {

    CLIENT("client", "客户端"),
    AGENT("agent", "代理端"),
    SITE("site", "站点端"),
    CENTER_ADMIN("center", "总控端"),
    BUSINESS("business", "商务端"),
    CLIENT_GUEST("guest", "客户端游客无需登录");

    private final String code;
    private final String name;

    ClientTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    private static Map<String, ClientTypeEnum> cache;

    static {
        cache = Arrays.stream(ClientTypeEnum.values()).collect(Collectors.toMap(ClientTypeEnum::getCode, Function.identity()));
    }

    public static ClientTypeEnum of(String code) {
        return cache.get(code);
    }
}
