package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum ClientGameInfoLabelEnum {
    NULL(0,"无"),
    HOT_RECOMMENDED(1, "热门-推荐"),
    NEWS(2, "新游戏"),
    ;
    private Integer code;
    private String name;

    ClientGameInfoLabelEnum(Integer code, String name) {
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

    public static ClientGameInfoLabelEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ClientGameInfoLabelEnum[] types = ClientGameInfoLabelEnum.values();
        for (ClientGameInfoLabelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        ClientGameInfoLabelEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<ClientGameInfoLabelEnum> getList() {
        return Arrays.asList(values());
    }

}
