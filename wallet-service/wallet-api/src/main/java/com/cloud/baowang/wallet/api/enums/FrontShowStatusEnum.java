package com.cloud.baowang.wallet.api.enums;

public enum FrontShowStatusEnum {
    HIDE(0, "隐藏"),
    SHOW(1, "显示"),
            ;

    private Integer code;
    private String name;

    FrontShowStatusEnum(Integer code, String name) {
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
