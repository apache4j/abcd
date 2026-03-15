package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

public enum StatusEnum {
    OPEN(1, "开启中"),
    MAINTAIN(2, "维护中"),
    CLOSE(3, "已禁用"),
    ;

    private Integer code;
    private String name;

    StatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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

    public static StatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        StatusEnum[] types = StatusEnum.values();
        for (StatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        StatusEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<StatusEnum> getList() {
        return Arrays.asList(values());
    }

}
