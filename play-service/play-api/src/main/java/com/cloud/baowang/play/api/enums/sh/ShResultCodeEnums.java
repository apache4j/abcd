package com.cloud.baowang.play.api.enums.sh;
import lombok.Getter;

@Getter
public enum ShResultCodeEnums {
    SUCCESS("0", "成功"),
    SIGN_ERROR("20005", "Md5签名错误"),
    PARAM_ERROR("9998", "参数错误"),
    REPEAT_TRANSACTIONS("1013", "订单已存在,请勿重复提交"),
     SERVER_INTERNAL_ERROR("9000", "其他异常"),
    INSUFFICIENT_BALANCE("20001", "玩家余额不足");
    private final String code;
    private final String message;
    ShResultCodeEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
