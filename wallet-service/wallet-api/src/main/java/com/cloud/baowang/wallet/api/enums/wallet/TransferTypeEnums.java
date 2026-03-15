package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferTypeEnums {
    IN(1, "转入"),

    OUT(2, "转出"),
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
