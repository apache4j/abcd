package com.cloud.baowang.system.api.enums;

import lombok.Getter;

@Getter
public enum TransferStatusEnum {

    SUCCESS(0, "成功"),

    FAIL(1, "失败"),
    ;

    private Integer code;

    private String name;

    TransferStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TransferStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TransferStatusEnum[] types = TransferStatusEnum.values();
        for (TransferStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
