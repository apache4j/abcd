package com.cloud.baowang.system.api.enums.areaLimit;

public enum AreaLimitTypeEnum {
    IP(1, "ip"),
    COUNTRY(2, "国家"),
    ;

    private Integer code;
    private String name;

    AreaLimitTypeEnum(Integer code, String name) {
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
}
