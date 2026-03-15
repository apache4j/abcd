package com.cloud.baowang.play.api.enums.acelt;

import lombok.Getter;

@Getter
public enum ACELTResultCodeEnums {
    SUCCESS(0, "成功"),
    INVALID_INPUT(39001, "入参为空"),
    MD5_VERIFICATION_FAILED(39041, "md5签名验证失败"),
    INVALID_USER_DATA(39027, "用户数据无效"),
    INVALID_TRANSACTION_TYPE(39013, "收入或支出类型有误！"),
    INVALID_CURRENCY(39040, "币种有误！"),
    INSUFFICIENT_BALANCE(39028, "用户余额不足！"),
    DATA_SAVE_FAILED(39038, "保存数据失败！"),
    SYSTEM_ERROR(500, "服务器错误，请联系管理员！"),
    USER_FROZEN(8009, "用户已被冻结"),
    GAME_LOCK(8010, "游戏锁定"),
    INVALID_REQUEST(1034, "无效请求"),
    DATA_NOT_EXIST(10013, "数据不存在");


    private final Integer code;
    private final String message;

    ACELTResultCodeEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
