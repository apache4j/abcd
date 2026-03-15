package com.cloud.baowang.system.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 * 菜单类型（1 目录 2 菜单 9 按钮）
 */

public enum BusinessMenuTypeEnum {

    DIRECTORY(1, "目录"),

    MENU(2, "菜单"),

    BUTTON(9, "按钮"),
    ;

    private Integer code;
    private String name;

    BusinessMenuTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BusinessMenuTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        BusinessMenuTypeEnum[] types = BusinessMenuTypeEnum.values();
        for (BusinessMenuTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<BusinessMenuTypeEnum> getList() {
        return Arrays.asList(values());
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
