package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum CornerLabelsEnum {
    NULL(0,"无"),
    NEWS(1, "NEWS"),
    HOT(2, "HOT"),
    ;
    private Integer code;
    private String name;

    CornerLabelsEnum(Integer code, String name) {
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

    public static CornerLabelsEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        CornerLabelsEnum[] types = CornerLabelsEnum.values();
        for (CornerLabelsEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        CornerLabelsEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<CornerLabelsEnum> getList() {
        return Arrays.asList(values());
    }

}
