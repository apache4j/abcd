package com.cloud.baowang.system.api.enums.tutorial;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ChangeTypeEnum {

    TUTORIAL_NAME(0, "教程名称"),
    TABS_NAME(1, "页签名称"),
    TUTORIAL_ICON(2, "教程图标"),
    TABS_ICON(3, "页签图标"),
    TUTORIAL_STATUS(4, "教程状态"),
    TABS_STATUS(5, "页签状态"),
    TUTORIAL_CONTENT(6, "教程内容"),

    ;

    private Integer code;

    private String name;

    ChangeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ChangeTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ChangeTypeEnum[] types = ChangeTypeEnum.values();
        for (ChangeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ChangeTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
