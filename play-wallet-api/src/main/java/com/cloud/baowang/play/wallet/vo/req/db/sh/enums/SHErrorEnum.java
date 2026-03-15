package com.cloud.baowang.play.wallet.vo.req.db.sh.enums;
public enum SHErrorEnum {

    SUCCESS(200, "Success"),
    MEMBER_NOT_EXIST(1000, "会员不存在"),
    MEMBER_STATUS_ERROR(1001, "验证会员状态错误"),
    INSUFFICIENT_BALANCE(1002, "余额不足"),
    BET_NOT_ACCEPTED(1003, "此时不接受投注"),
    SIGNATURE_ERROR(8000, "签名验证失败"),
    OTHER_EXCEPTION(9000, "其他异常"),
    MEMBER_ACCOUNT_ERROR(30011, "会员账号有误"),
    UNSUPPORTED_WALLET_TYPE(30016, "当前钱包类型不支持接口"),
    SERVICE_UNAVAILABLE(98888, "无法正常提供服务"),
    GAME_ACCOUNT_NOT_EXIST(20001, "游戏账号不存在"),
    PLAYER_FUND_ACCOUNT_NOT_EXIST(30003, "玩家资金账户不存在"),
    ACCOUNT_DISABLED(20008, "账号已停用"),
    BUSINESS_LOGIC_ERROR(91111, "逻辑业务异常"),
    PARAMETER_ERROR(90000, "参数错误"),
    SYSTEM_ERROR(99999, "其他系统错误");

    private final int code;
    private final String msg;

    SHErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static SHErrorEnum fromCode(int code) {
        for (SHErrorEnum e : SHErrorEnum.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null; // 或者抛异常
    }
}
