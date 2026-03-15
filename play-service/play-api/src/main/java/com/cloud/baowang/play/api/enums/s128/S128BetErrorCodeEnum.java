package com.cloud.baowang.play.api.enums.s128;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S128BetErrorCodeEnum {
    SUCCESS("00", "OK","Successfully executed"),
    INSUFFICIENT_FUND("88", "err: insufficient fund", "Insufficient fund to bet"),
    LOGIN_NOT_FOUND("99", "err: player suspended", "Player account suspended"),
    OTHER_ERR("All Other Code", "error:", "General error"),
    ;

    private final String code;
    private final String message;
    private final String desc;



    public static S128BetErrorCodeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        S128BetErrorCodeEnum[] types = S128BetErrorCodeEnum.values();
        for (S128BetErrorCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
