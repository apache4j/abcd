package com.cloud.baowang.play.api.enums.s128;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S128GetBalanceErrorCodeEnum {
    SUCCESS("00", "OK","Successfully executed"),
    LOGIN_NOT_FOUND("99", "err: login id not found", "Login ID not found"),
    OTHER_ERR("All Other Code", "error:", "General error"),
    ;

    private final String code;
    private final String message;
    private final String desc;



    public static S128GetBalanceErrorCodeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        S128GetBalanceErrorCodeEnum[] types = S128GetBalanceErrorCodeEnum.values();
        for (S128GetBalanceErrorCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
