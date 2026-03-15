package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum ACELTTransferTypeEnum {

    PT(1, "普通投注"),
    ZH(2, "追号投注"),
    JJ(3, "奖金派发"),
    HJ(4, "和局退款"),
    GR(5, "个人撤单"),
    XT(6, "系统撤单");

    private final Integer code;  // 交易类型代码
    private final String description;  // 交易类型描述

    // 构造函数
    ACELTTransferTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    public static ACELTTransferTypeEnum fromCode(Integer code) {
        for (ACELTTransferTypeEnum type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }



}
