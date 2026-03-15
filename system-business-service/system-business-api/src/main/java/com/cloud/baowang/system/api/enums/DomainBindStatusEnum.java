package com.cloud.baowang.system.api.enums;

import lombok.Getter;

@Getter
public enum DomainBindStatusEnum {

    UN_BIND(0, "未绑定"),
    BIND(1, "已绑定"),
    ;

    private Integer code;
    private String name;

    DomainBindStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
