package com.cloud.baowang.system.api.enums;


import lombok.Getter;

@Getter
public enum BusinessSystemEnum {


    ADMIN_CENTER("ADMIN_CENTER", "总台"),

    SITE("SITE", "站点"),

    ;

    private String code;
    private String name;

    BusinessSystemEnum(String code, String name) {
        this.code = code;
        this.name = name;
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
