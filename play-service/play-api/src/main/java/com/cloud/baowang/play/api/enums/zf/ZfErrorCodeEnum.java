package com.cloud.baowang.play.api.enums.zf;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ZfErrorCodeEnum {
    SUCCESS(0, "Success","查询成功"),
    TOKEN_EXPIRED(4, "Token expired", "Api access token 已过期或无效"),
    OTHER_ERR(5, "Other error","其他错误"),
    ;

    private final int code;
    private final String message;
    private final String desc;



    public static ZfErrorCodeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ZfErrorCodeEnum[] types = ZfErrorCodeEnum.values();
        for (ZfErrorCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
