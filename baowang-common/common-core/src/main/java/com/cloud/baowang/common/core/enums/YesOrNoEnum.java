package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @Author qiqi
 */
public enum YesOrNoEnum {
    NO("0", "否"),
    YES("1", "是"),

    ;
    private String code;
    private String name;

    YesOrNoEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static YesOrNoEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        YesOrNoEnum[] types = YesOrNoEnum.values();
        for (YesOrNoEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<YesOrNoEnum> getList() {
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
