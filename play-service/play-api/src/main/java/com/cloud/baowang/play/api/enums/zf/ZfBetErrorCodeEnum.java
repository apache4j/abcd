package com.cloud.baowang.play.api.enums.zf;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ZfBetErrorCodeEnum {
    SUCCESS(0, "Success","查询成功"),
    ALREADY_ACCEPTED(1, "Already accepted", "该注单已承认"),
    NOT_ENOUGH_BALANCE(2, "Not enough balance", "玩家余额不足"),
    INVALID_PARAMETER(3, "Invalid parameter", "参数无效 (详情请放到 message 字段)"),
    TOKEN_EXPIRED(4, "Token expired","Api access token 已过期或无效"),
    OTHER_ERR(5, "Other error","其他错误(详情请放到 message 字段)"),
    ;

    private final int code;
    private final String message;
    private final String desc;



    public static ZfBetErrorCodeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ZfBetErrorCodeEnum[] types = ZfBetErrorCodeEnum.values();
        for (ZfBetErrorCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
