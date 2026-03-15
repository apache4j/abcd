package com.cloud.baowang.play.api.enums.sa;
import lombok.Getter;

@Getter
public enum SAResultCodeEnums {

    SUCCESS(0, "成功"),
    USERNAME_LENGTH_OR_FORMAT_ERROR(108, "用户名长度或者格式错误"),
    USERNAME_ALREADY_EXISTS(113, "用户名已存在"),
    CURRENCY_NOT_EXIST(114, "币种不存在"),
    ACCOUNT_CREATION_FAILED(133, "建立帐户失败"),
    ACCOUNT_NOT_FOUND(1000, "会员帐号不存在"),
    INVALID_CURRENCY(1001, "货币代码不正确"),
    INVALID_AMOUNT(1002, "金额不正确"),
    ACCOUNT_LOCKED(1003, "会员帐号已被锁"),
    INSUFFICIENT_FUNDS(1004, "不足够点数"),
    GENERAL_ERROR(1005, "一般错误"),
    DECRYPTION_ERROR(1006, "解密错误"),
    SESSION_EXPIRED(1007, "登入时段过期，需要重新登入"),
    SYSTEM_ERROR(9999, "系统错误");

    private final Integer code;
    private final String message;
    SAResultCodeEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
