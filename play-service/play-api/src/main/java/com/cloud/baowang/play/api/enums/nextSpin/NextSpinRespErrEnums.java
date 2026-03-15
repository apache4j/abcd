package com.cloud.baowang.play.api.enums.nextSpin;

import lombok.Getter;

@Getter
public enum NextSpinRespErrEnums {

    // 成功
    SUCCESS(0, "成功"),

    // 系统相关错误 (1-99)
    SYSTEM_ERROR(1, "系统错误"),
    INVALID_REQUEST(2, "非法请求"),
    SERVICE_UNAVAILABLE(3, "服务暂时不可用"),
    REQUEST_TIMEOUT(100, "请求超时"),
    USER_CALL_LIMIT_EXCEEDED(101, "用户调用次数超限"),
    REQUEST_FORBIDDEN(104, "请求被禁止"),
    MISSING_REQUIRED_PARAM(105, "缺少必要的参数"),
    INVALID_PARAM(106, "非法的参数"),
    DUPLICATE_BATCH_NO(107, "批次号重复"),
    DUPLICATE_ID_NO(109, "ID 重复或没有找到"),
    INVALID_LOGIN_KEY(108, "Apk 版本及 Pc 版本商家登录 Key 错误"),
    BATCH_NO_NOT_EXIST(110, "批次号不存在"),
    MERCHANT_NOT_EXIST(10113, "Merchant 不存在"),
    API_CALL_LIMIT_EXCEEDED(112, "Api 调用次数超限"),
    INVALID_ACCT_ID(113, "Acct Id 不正确"),

    // 会员/账号相关错误 (101xx)
    MEMBER_PASSWORD_INCORRECT(10104, "会员密码不正确"),
    MEMBER_NOT_EXIST(10103, "会员不存在"),
    ACCOUNT_NOT_ACTIVATED(10105, "账号未激活"),
    ACCOUNT_LOCKED(10110, "账号已锁"),
    ACCOUNT_SUSPENDED(10111, "帐号 Suspend"),

    // 交易/下注相关错误 (111xx)
    INSUFFICIENT_BALANCE(11101, "余额不足"),
    BET_INVALID(11102, "投注失效"),
    GAME_NOT_OPEN(11103, "玩法未开放"),
    INCOMPLETE_BET_INFO(11104, "下注信息不完整"),
    ACCOUNT_INFO_ABNORMAL(11105, "帐号信息异常"),
    ILLEGAL_BET_REQUEST(11108, "下注请求不合法"),
    SETUP_INCOMPLETE(12001, "设置不完整"),
    BET_AMOUNT_EXCEEDS_MAX(1110801, "下注最大值超过上限"),
    BET_AMOUNT_BELOW_MIN(1110802, "下注最小值超下限"),
    INVALID_BET_AMOUNT(1110803, "下注金额错误");


    private final Integer code;
    private final String description;

    NextSpinRespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
