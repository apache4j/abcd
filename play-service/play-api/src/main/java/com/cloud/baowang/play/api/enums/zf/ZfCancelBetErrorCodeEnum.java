package com.cloud.baowang.play.api.enums.zf;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ZfCancelBetErrorCodeEnum {
    SUCCESS(0, "Success","取消成功"),
    ALREADY_CANCELED(1, "Already canceled", "该注单已取消"),
    ROUND_NOT_FOUND(2, "Round not found", "注单无效"),
    INVALID_PARAMETER(3, "Invalid parameter", "参数无效 (详情请放到 message 字段)"),
    TOKEN_EXPIRED(4, "Token expired","Api access token 已过期或无效"),
    OTHER_ERR(5, "Other error","其他错误(详情请放到 message 字段)"),
    CANNOT_CANCELED(6, "Already accepted and cannot be canceled","注单已成立而不可取消"),
    ;

    private final int code;
    private final String message;
    private final String desc;



    public static ZfCancelBetErrorCodeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ZfCancelBetErrorCodeEnum[] types = ZfCancelBetErrorCodeEnum.values();
        for (ZfCancelBetErrorCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
