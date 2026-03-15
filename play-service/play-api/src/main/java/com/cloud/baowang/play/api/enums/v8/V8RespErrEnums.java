package com.cloud.baowang.play.api.enums.v8;

import lombok.Getter;

@Getter
public enum V8RespErrEnums {

    SUCCESS(0, "成功"),
    INSUFFICIENT_BALANCE(1, "余额不足"),

    PLAYER_NOT_EXIST(2, "玩家不存在。"),

    MAINTAINS(3, "维护中"),

    TOKEN_VALIDATION_ERROR(4, "token验证失败"),

    DATEA_FORMAT_ERROR(5, "数据格式或参数数据错误缺失"),

    DECRYPTION_ERROR(6, "解密错误"),

    MD5_KEY_ERROR(7, "MD5 错误"),

    DUPLICATE_ORDER(9, "订单编号重复"),


    AGENT_EXIST_NOT(10, "代理不存在"),

    ORDER_NOT_EXIST(12, "查无订单号"),

    FIAILURE_PLAYER(13, "服务器错误"),

    VENUE_CLOSEED(32, "获取玩家信息失败"),

    WALLET_NOT_EXIST(33, "钱包不存在"),

    CANCEL_NOT_EXIST(34, "账变已取消"),


    ;

    private final Integer code;
    private final String description;

    V8RespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
