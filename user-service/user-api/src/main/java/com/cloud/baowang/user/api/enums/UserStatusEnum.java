package com.cloud.baowang.user.api.enums;

import java.util.Arrays;
import java.util.List;

public enum UserStatusEnum {

    NORMAL("1", "正常"),
    LOGIN_LOCK("2", "登录锁定"),
    GAME_LOCK("3", "游戏锁定"),
    PAY_LOCK("4", "充提锁定"),
    RISK_IP("5", "风险IP"),
    ;

    private String code;
    private String name;

    UserStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        UserStatusEnum[] types = UserStatusEnum.values();
        for (UserStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserStatusEnum> getList() {
        return Arrays.asList(values());
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

}
