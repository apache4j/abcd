package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NotificationTypeEnum {
    ALL(0,"全部"),
    ANNOUNCEMENT(1,"公告"),
    EVENTS(2,"活动"),
    NOTIFICATION(3,"通知"),

    SYSTEM_MESSAGE(4,"系统消息");



    private final int code;
    private final String name;

    NotificationTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static NotificationTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        NotificationTypeEnum[] types = NotificationTypeEnum.values();
        for (NotificationTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
    public static List<Integer> getAllCodes(){
        return Arrays.stream(NotificationTypeEnum.values()).map(NotificationTypeEnum::getCode).collect(Collectors.toList());
    }
}
