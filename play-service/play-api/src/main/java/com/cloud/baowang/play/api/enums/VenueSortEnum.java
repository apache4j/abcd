package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum VenueSortEnum {
    RECOMMENDED(1, "你可能喜欢"),
    POPULAR(2, "最受欢迎的"),
    NEW(3, "最新的"),
    ASC(4, "A-Z-顺序"),
    DESC(5, "Z-A-倒叙"),
    ;
    private Integer code;
    private String name;

    VenueSortEnum(Integer code, String name) {
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

    public static VenueSortEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        VenueSortEnum[] types = VenueSortEnum.values();
        for (VenueSortEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        VenueSortEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<VenueSortEnum> getList() {
        return Arrays.asList(values());
    }

}
