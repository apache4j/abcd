package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportResultCodeEnum {

    // 通用
    SUCCESS("0000", "成功"),
    PARAM_INVALID("1001", "参数不合法"),
    GAME_ORDER_NOT_FOUND("1002", "游戏订单号不存在"),
    SIGN_VERIFY_FAIL("1003", "验签失败"),

    // 商户 & 玩家
    MERCHANT_NOT_FOUND("2001", "没有此商户"),
    PLAYER_NOT_FOUND("2002", "没有此玩家"),
    PLAYER_NAME_DUPLICATE("2003", "玩家名称重复"),
    PLAYER_DISABLED("2004", "此玩家已被禁用"),
    PLAYER_NAME_INVALID("2005", "玩家名称不合法"),
    DUPLICATE_SUBMIT("2006", "请勿重复提交"),

    // 投注相关
    PLAYER_BALANCE_NOT_ENOUGH("3001", "玩家余额不足"),
    ORDER_DUPLICATE("3002", "订单号重复(投注)"),
    INVALID_BET_AMOUNT("3003", "无效的金额(投注)"),

    // 登录相关
    LOGIN_REQUEST_TIMEOUT("4001", "登录请求时间过久(过当前时间24小时)"),
    LOGIN_FAIL("4002", "登录失败"),
    USER_NOT_LOGIN("4003", "用户没有登录"),
    TOKEN_EXPIRED("4004", "用户TOKEN已经过期"),
    TOKEN_REQUIRED("4005", "用户TOKEN必传"),
    LOGIN_CLOSED("4006", "登录注册入口已关闭"),

    // 安全验证
    SIGN_FAIL("5001", "验签失败"),
    IP_VERIFY_FAIL("5002", "验证IP失败"),

    // 转账相关
    INVALID_TRANSFER_AMOUNT("6001", "无效的金额（转入或转出）"),
    TRANSFER_TYPE_INVALID("6002", "转账类型不正确"),
    MERCHANT_TRANSFER_MODE_ERROR("6003", "商户转点模式错误"),
    TRANSFER_ORDER_NOT_FOUND("6004", "交易订单号不存在"),
    NO_WALLET_BALANCE_QUERY("6005", "panda不提供免转钱包查询余额"),

    // 系统级
    SYSTEM_ERROR("9001", "系统错误"),
    RATE_LIMIT("9002", "限流"),
    ID_REQUIRED("9003", "ID不能为空");

    private final String code;
    private final String message;

    DbPanDaSportResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
