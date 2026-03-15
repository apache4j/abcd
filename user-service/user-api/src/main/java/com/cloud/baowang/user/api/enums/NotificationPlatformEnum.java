package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知配置， 区分平台
 */
@Getter
public enum NotificationPlatformEnum {
    USER_PLATFORM(1,"会员通知"),
    AGENT_PLATFORM(2,"代理通知");



    private final int code;
    private final String name;

    NotificationPlatformEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static NotificationPlatformEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        NotificationPlatformEnum[] types = NotificationPlatformEnum.values();
        for (NotificationPlatformEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
    public static List<Integer> getAllCodes(){
        return Arrays.stream(NotificationPlatformEnum.values()).map(NotificationPlatformEnum::getCode).collect(Collectors.toList());
    }
}
