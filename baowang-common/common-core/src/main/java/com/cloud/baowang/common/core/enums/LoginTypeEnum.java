package com.cloud.baowang.common.core.enums;

public enum LoginTypeEnum {
    SUCCESS(0, "登录成功"),
    FAIL(1, "登录失败");

    private Integer code;
    private String name;

    LoginTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        LoginTypeEnum[] types = LoginTypeEnum.values();
        for (LoginTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
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
}
