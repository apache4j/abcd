package com.cloud.baowang.play.wallet.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DBFishingChessErrorEnum {
    // ================= 通用 =================
    SUCCESS(1000, "操作成功"),
    PARAM_ERROR(1001, "参数异常"),
    PARAM_PARSE_ERROR(1002, "参数解析异常"),
    SYSTEM_TIMEOUT(1003, "系统超时"),

    USER_NOT_FOUND(10001, "玩家账号不存在或玩家账号状态异常"),
    BALANCE_NOT_ENOUGH(10002, "余额不足"),
    ORDER_SUCCEED(10003, "订单号已成功"),
    ORDER_FORMAT_ERROR(10005, "订单号格式错误"),

    SIGN_ERROR(10006, "验签错误"),

    UNKNOWN_ERROR(10007, "未知错误"),

    //
    ORDER_NOT_FOUND(10201, "交易订单不存在"),
    SIGN_ERROR_2(10202, "验签错误"),
    SYSTEM_MAINTAINED_2(10203, "内部服务器错误"),


    // ================= 登录相关 =================
    ACCOUNT_FROZEN(2001, "当前玩家账号已冻结，请联系游戏客服"),
    TEST_ACCOUNT_DISABLED(2002, "测试账号已停用"),
    TEST_ACCOUNT_INVALID(2003, "测试账号失效"),
    TOKEN_GENERATE_ERROR(2004, "token 生成异常"),
    PLAYER_DATA_EMPTY(2005, "玩家数据不能为空"),
    PASSWORD_VERIFY_FAILED(2006, "玩家账号的密码，验证失败"),
    MERCHANT_CODE_EMPTY(2007, "商户编号不能为空"),
    MERCHANT_CODE_ERROR(2008, "商户编号错误"),
    MERCHANT_INFO_MISSING(2009, "请配置相关商户信息"),
    SIGN_MD5_FAILED(2010, "参数MD5 验签失敗"),

    // ================= 游戏相关 =================
    PLAYER_IN_OTHER_GAME(8001, "玩家在其它游戏中"),
    GAME_NOT_EXIST(8002, "游戏(gameId)不存在"),
    GAME_ID_DUPLICATE(8003, "游戏列表 gameId 重复"),

    // ================= 拉取游戏注单 =================
    QUERY_TOO_FREQUENT(7001, "查询请求过于频繁"),
    QUERY_TIME_INVALID(7002, "查询时间超过规范时间"),
    QUERY_TIME_TOO_LONG(7003, "查询时间区间过长"),
    PAGE_SIZE_INVALID(7004, "每页条数要在 1000 到 10000 之间");

    private final int code;
    private final String msg;

    /**
     * 根据 code 获取枚举
     */
    public static DBFishingChessErrorEnum fromCode(int code) {
        for (DBFishingChessErrorEnum e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
