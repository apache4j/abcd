package com.cloud.baowang.play.api.enums;

public enum ChangeStatusEnum {
    NOT_CHANGE(0, "未变更"),
    CHANGED(1, "已变更");

    private Integer code;
    private String name;

    ChangeStatusEnum(Integer code, String name) {
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

    public static ChangeStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ChangeStatusEnum[] types = ChangeStatusEnum.values();
        for (ChangeStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
