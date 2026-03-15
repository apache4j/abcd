package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum DGErrorEnum {

    SUCCESS(0, "操作成功", "Success"),
    PARAMETER_ERROR(1, "参数错误", "Parameter Error"),
    TOKEN_VERIFICATION_FAILED(2, "Token验证失败", "Token Verification Failed"),
    ILLEGAL_OPERATION(4, "非法操作", "Illegal Operation"),
    DATE_FORMAT_ERROR(10, "日期格式错误", "Date format error"),
    DATA_FORMAT_ERROR(11, "数据格式错误", "Data format error"),
    PERMISSION_DENIED(97, "没有权限", "Permission denied"),
    OPERATION_FAILED(98, "操作失败", "Operation failed"),
    UNKNOWN_ERROR(99, "未知错误", "Unknown Error"),
    ACCOUNT_LOCKED(100, "账号被锁定", "Account is locked"),
    ACCOUNT_FORMAT_ERROR(101, "账号格式错误", "Account format error"),
    ACCOUNT_TAKEN(103, "此账号被占用", "This account is taken"),
    PASSWORD_FORMAT_ERROR(104, "密码格式错误", "Password format error"),
    PASSWORD_WRONG(105, "密码错误", "Password wrong"),
    NEW_OLD_PASSWORD_SAME(106, "新旧密码相同", "New & Old Password is the same"),
    MEMBER_ACCOUNT_UNAVAILABLE(107, "会员账号不可用", "Member account unavailable"),
    LOGIN_FAILURE(108, "登入失败", "Login Failure"),
    SIGNUP_FAILURE(109, "注册失败", "Signup Failure"),
    NOT_AN_AGENT(113, "传入的代理账号不是代理", "The Agent account inputted is not an Agent account"),
    MEMBER_NOT_FOUND(114, "找不到会员", "Member not found"),
    ACCOUNT_OCCUPIED(116, "账号已占用", "Account occupied"),
    AGENT_NOT_FOUND(118, "找不到指定的代理", "Can not find the specified Agent"),
    AGENT_INSUFFICIENT_FUNDS(119, "存取款操作时代理点数不足", "Insufficent funds during Agent withdrawal"),
    INSUFFICIENT_BALANCE(120, "余额不足", "Insufficient balance"),
    PROFIT_LIMIT_INVALID(121, "盈利限制必须大于或等于0", "Profit limit must be greater than or equal to 0"),
    DEMO_ACCOUNT_EXHAUSTED(150, "免费试玩账号用完", "Ran out of free demo accounts"),
    REGISTRATION_LIMIT_EXCEEDED(188, "注册新会员超出,请联系客服", "Registration of new members is exceeded, please contact customer service"),
    SYSTEM_MAINTENANCE(300, "系统维护", "System maintenance"),
    AGENT_ACCOUNT_NOT_FOUND(301, "代理账号找不到", "Agent account not found"),
    LIMIT_GROUP_NOT_FOUND(321, "找不到相应的限红组", "Limit Group Not Found"),
    CURRENCY_NOT_FOUND(322, "找不到指定的货币类型", "Currency Name Not Found"),
    TRANSFER_SERIAL_USED(323, "转账流水号占用", "Used serial numbers for Transfer"),
    TRANSFER_FAILED(324, "转账失败", "Transfer failed"),
    AGENT_STATUS_UNAVAILABLE(325, "代理状态不可用", "Agent Status Unavailable"),
    CLIENT_IP_RESTRICTED(400, "客户端IP 受限", "Client IP Restricted"),
    NETWORK_LATENCY(401, "网络延迟", "Network latency"),
    CLIENT_SOURCE_LIMITED(403, "客户端来源受限", "Clients limited sources"),
    RESOURCE_NOT_FOUND(404, "请求的资源不存在", "Resource requested does not exist"),
    TOO_FREQUENT_REQUESTS(405, "请求太频繁", "Too frequent requests"),
    REQUEST_TIMEOUT(406, "请求超时", "Request timed out"),
    GAME_ADDRESS_NOT_FOUND(407, "找不到游戏地址", "Can not find game address"),
    SYSTEM_ERROR_500(500, "系统异常", "System Error"),
    SYSTEM_ERROR_501(501, "系统异常", "System Error"),
    SYSTEM_ERROR_502(502, "系统异常", "System Error"),
    SYSTEM_ERROR_503(503, "系统异常", "System Error");

    private final int code;
    private final String messageZh;
    private final String messageEn;

    DGErrorEnum(int code, String messageZh, String messageEn) {
        this.code = code;
        this.messageZh = messageZh;
        this.messageEn = messageEn;
    }


    public static DGErrorEnum fromCode(int code) {
        for (DGErrorEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
