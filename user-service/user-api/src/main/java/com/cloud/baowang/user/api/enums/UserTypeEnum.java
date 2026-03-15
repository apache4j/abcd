package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum UserTypeEnum {
    TEST(1, "测试"),
    FORMAL(2, "正式"),
    ;

    private final Integer code;
    private final String name;

    UserTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserTypeEnum[] types = UserTypeEnum.values();
        for (UserTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static List<UserTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
