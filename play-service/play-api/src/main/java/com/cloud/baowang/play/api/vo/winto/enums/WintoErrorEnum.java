package com.cloud.baowang.play.api.vo.winto.enums;
public enum WintoErrorEnum {

    SUCCESS(200,"成功"),
    SYSTEM_BUSY(500, "系统繁忙"),
    INTERNAL_ERROR(502, "内部错误"),
    ILLEGAL_PARAMETERS(511, "违法参数"),
    USER_NOT_EXIST(512, "用户不存在"),
    GAME_NOT_EXIST(513, "游戏不存在"),
    MERCHANT_NOT_EXIST(514, "商户不存在"),
    MERCHANT_GAME_NOT_EXIST(515, "游戏不存在"),
    UNAUTHORIZED_GAME(516, "游戏未受权"),
    MERCHANT_DISABLED(517, "商户已禁用"),
    MERCHANT_EXPIRED(518, "商户已过期"),
    QUERY_DATE_TOO_LONG(519, "查询日期过长"),
    ILLEGAL_IP(520, "非法IP"),
    NO_PERMISSION(521, "无权限"),
    API_NOT_CONFIGURED(522, "API接口未配置"),
    INVALID_SIGNATURE(523, "无效签名"),
    INTERFACE_NOT_EXIST(524, "接口不存在"),
    INSUFFICIENT_BALANCE(525, "用户余额不足"),
    BETS_FAIL(526, "投注失败"),
    VOID_BETS(527, "无效投注");

    private final int code;
    private final String msg;

    WintoErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static WintoErrorEnum fromCode(int code) {
        for (WintoErrorEnum e : WintoErrorEnum.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null; // 或者抛异常
    }
}
