package com.cloud.baowang.play.api.enums.omg;

import lombok.Getter;

@Getter
public enum OmgRespErrEnums {

    SUCCESS(1, "成功"),
    INSUFFICIENT_BALANCE(10001, "余额不足，下分失败"),
    SIGNATURE_VERIFICATION_FAILED(10002, "签名验证未通过"),
    ORDER_PROCESSING(10003, "订单正在处理中"),
    ORDER_NOT_FOUND(10004, "订单不存在"),
    DUPLICATE_ORDER(10005, "订单重复"),
    REQUEST_TOO_FREQUENT(10006, "请求过于频繁"),
    CANNOT_CHANGE_BALANCE_IN_GAME(10007, "游戏中不能上下分"),
    GAME_CLOSED(10008, "游戏已关闭"),
    ILLEGAL_PARAMETER(10009, "非法参数"),
    FAILURE(10010, "失败"),
    LOGIN_TOKEN_FAILED(10011, "玩家登录token验证失败"),
    ILLEGAL_APP_ID(10012, "非法的app_id"),
    PLAYER_NOT_FOUND(10013, "玩家不存在"),
    PLAYER_BANNED(10014, "玩家被禁止登录"),
    SERVER_INTERNAL_ERROR(10015, "服务器内部错误"),
    GAME_NOT_FOUND(10016, "游戏不存在"),
    GAME_NOT_LAUNCHED(10017, "游戏未开通"),
    MERCHANT_BANNED(10018, "商户已被禁用"),
    UNSUPPORTED_CURRENCY(10019, "不支持的货币10"),
    TRANSFER_TRANSACTION_NOT_FOUND(10020, "转账交易不存在"),
    INVALID_MERCHANT_WALLET_TYPE(10021, "商户钱包类型不正确"),
    MERCHANT_NOT_FOUND_FOR_APP_ID(10022, "传入的appid对应的商户不存在"),
    PARAMETER_TYPE_MISMATCH(10023, "参数类型不匹配");

    private final Integer code;
    private final String description;

    OmgRespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
