package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HandleStatusEnums {
    OK(0, "正常"),

    ERROR(1, "异常"),
    ;

    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;
}
