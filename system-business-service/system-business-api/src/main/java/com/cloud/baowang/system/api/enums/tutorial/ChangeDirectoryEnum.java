package com.cloud.baowang.system.api.enums.tutorial;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ChangeDirectoryEnum {

    CATEGOPORY(0, "教程大类"),

    TABS(1, "教程页签"),

    CLASS(2,"教程分类"),

    CONTENT(3,"教程内容")
    ;

    private Integer code;

    private String name;

    ChangeDirectoryEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ChangeDirectoryEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ChangeDirectoryEnum[] types = ChangeDirectoryEnum.values();
        for (ChangeDirectoryEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ChangeDirectoryEnum> getList() {
        return Arrays.asList(values());
    }
}
