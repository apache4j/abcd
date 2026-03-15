package com.cloud.baowang.play.wallet.enums;

public enum AceltErrorEnum {
    SUCCESS(0, "成功"),
    FAIL(500, "失败/请联系管理员"),
    UNAUTHORIZED(401, "权限不足"),
    PARAM_ERROR(405, "参数异常、参数转换异常"),
    MEMBER_ACCOUNT_INVALID(1, "会员账号不合法"),
    MEMBER_TYPE_INVALID(2, "会员类型不合法"),
    MEMBER_PASSWORD_INVALID(3, "会员密码不合法"),
    MERCHANT_ACCOUNT_INVALID(4, "商户账号不合法"),
    DOUBLE_AGENT_REBATE_INVALID(5, "双面盘代理返点不合法"),
    STANDARD_AGENT_REBATE_INVALID(6, "标准盘代理返点不合法"),
    TIMESTAMP_INVALID(7, "时间戳不合法"),
    SIGN_INVALID(8, "签名不合法"),
    OPERATION_ERROR(501, "操作异常，具体消息可见相关接口返回的msg"),
    MEMBER_ALREADY_EXISTS(701, "会员已注册，请不要重复注册"),
    QUERY_MERCHANT_BONUS_FAIL(702, "查询商户奖金组数据失败"),
    QUERY_MERCHANT_DOUBLE_BONUS_FAIL(703, "查询商户双面盘奖金组数据失败"),
    QUERY_MERCHANT_STANDARD_BONUS_FAIL(704, "查询商户标准盘奖金组数据失败"),
    DOUBLE_REBATE_OUT_OF_RANGE(705, "商户双面盘代理模式下上送的彩种返点数据不在维护区间内"),
    STANDARD_REBATE_OUT_OF_RANGE(706, "商户标准盘代理模式下上送的彩种返点数据不在维护区间内"),
    VERIFY_SIGN_FAIL(707, "验证签名失败，请检查签名生成规则是否合法"),
    MODIFY_MEMBER_INFO_FAIL(708, "修改会员信息失败，请检查是否注册过该会员"),
    LOGIN_FAIL(709, "登录失败，请检查是否注册该会员"),
    DOUBLE_REBATE_INVALID(710, "商户双面盘代理模式下上送的彩种返点不合法"),
    STANDARD_REBATE_INVALID(711, "商户标准盘代理模式下上送的彩种返点不合法"),
    KICK_FAIL(712, "踢线失败，请检查是否注册该会员"),
    MEMBER_PASSWORD_ERROR(713, "会员登录密码错误，请检查会员维护的密码是否一致"),
    TRANSFER_FAIL(714, "转账失败，请检查是否注册该会员"),
    TRANSFER_OUT_FAIL(715, "转出失败，转出余额不足"),
    TRANSFER_RECORD_DUPLICATE(716, "会员转入转出记录重复，请检查"),
    QUERY_BALANCE_LOST(717, "查询用户余额丢失"),
    PARAM_INVALID(718, "参数不合法"),
    SUBMERCHANT_CREATE_FAIL(719, "子商户创建失败"),
    MERCHANT_NOT_EXIST(720, "商户不存在"),
    MERCHANT_ACCOUNT_TOO_LONG(721, "商户账号不能超过10位"),
    CHECK_APP_VERSION_FAIL(722, "检查app版本失败"),
    MEMBER_ACCOUNT_INIT_FAIL(723, "会员账户信息初始化失败"),
    MEMBER_INFO_INIT_FAIL(724, "会员信息初始化失败"),
    MEMBER_CONFIG_INIT_FAIL(725, "会员配置信息初始化失败"),
    MEMBER_LOGIN_RESTRICTED(726, "会员限制登录"),
    MEMBER_FUNDS_FROZEN(727, "会员资金冻结"),
    ONLY_TRANSFER_OUT(728, "只可资金转出"),
    FORBID_TRANSFER_OUT(729, "禁止资金转出"),
    MERCHANT_DISABLED(730, "商户已停用"),
    MERCHANT_ACCOUNT_RULE_INVALID(731, "商户账号由2-10位英文，数字跟下划线组成"),
    MERCHANT_NAME_RULE_INVALID(732, "商户名称由2-10位英文，数字跟下划线组成"),
    TRANSFER_TIMEOUT(733, "转账失败,处理超时"),
    QUERY_MEMBER_FAIL(735, "查询失败，请检查是否注册该会员"),
    SYSTEM_BUSY(777, "系统繁忙，请稍后再试"),
    TRANSFER_REPEAT(778, "请勿重复提交转账"),
    TRANSFER_IP_ERROR(779, "转帐失败，异常IP"),
    MERCHANT_IP_DISABLED(739, "商户IP停用"),
    MERCHANT_BALANCE_NOT_ENOUGH(740, "商户额度不足，请联系客服进行处理"),
    MERCHANT_TRANSFER_TYPE_ERROR(741, "商户转账方式错误"),

    // 资金类
    BALANCE_NOT_ENOUGH(1001, "余额不足，请充值"),
    UPDATE_BALANCE_FAIL(1002, "更新余额失败"),
    TRADE_TYPE_ERROR(1003, "交易类型参数有误"),
    MERCHANT_PARAM_INVALID(1004, "参数商户账号:merchant不合法"),
    MEMBER_PARAM_INVALID(1005, "参数会员账号:member 不合法");

    private final int code;
    private final String msg;

    AceltErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static AceltErrorEnum fromCode(int code) {
        for (AceltErrorEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
