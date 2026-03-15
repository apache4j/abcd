package com.cloud.baowang.activity.api.enums;

public enum ActivityEventStatusEnum {

    UNISSUED(0, "未发放"),
    ISSUED(1, "已发放");

    private Integer code;
    private String name;

    ActivityEventStatusEnum(Integer code, String name) {
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
