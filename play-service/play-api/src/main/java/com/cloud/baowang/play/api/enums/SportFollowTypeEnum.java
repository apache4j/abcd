package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum SportFollowTypeEnum {
    CHAMPION(1, "冠军"),
    EVENT(2, "赛事")
    ;
    private Integer code;
    private String name;

    SportFollowTypeEnum(Integer code, String name) {
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

    public static SportFollowTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        SportFollowTypeEnum[] types = SportFollowTypeEnum.values();
        for (SportFollowTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        SportFollowTypeEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<SportFollowTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
