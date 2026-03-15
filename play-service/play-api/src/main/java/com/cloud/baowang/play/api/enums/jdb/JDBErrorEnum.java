package com.cloud.baowang.play.api.enums.jdb;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 三方错误码
 */
@Getter
@NoArgsConstructor
public enum JDBErrorEnum {
    SUCCESS("0000", "成功", "Succeed"),
    FAILED("9999", "失败", "Failed"),
    NO_AUTH("9001", "无权限访问", "No authorized to access"),
    DOMAIN_NULL("9002", "域名为空或长度小于2", "Domain is null or the length of domain less than 2"),
    DOMAIN_INVALID("9003", "未通过域名验证", "Failed to pass the domain validation."),
    ENCRYPTED_DATA_NULL("9004", "加密数据为空或长度为0", "The encrypted data is null or the length of the encrypted data is equal to 0."),
    SAML_TIMESTAMP_INVALID("9005", "断言（SAML）未通过时间戳验证", "Assertion(SAML) didn’t pass the timestamp validation."),
    SAML_EXTRACTION_FAILED("9006", "无法从加密数据中提取 SAML 参数", "Failed to extract the SAML parameters from the encrypted data."),
    UNKNOWN_ACTION("9007", "未知操作", "Unknown action."),
    SAME_VALUE("9008", "与之前的值相同", "The same value as before."),
    TIMEOUT("9009", "请求超时", "Time out."),
    READ_TIMEOUT("9010", "读取超时", "Read time out."),
    DUPLICATE_TRANSACTION("9011", "重复交易", "Duplicate transactions."),
    TRY_AGAIN_LATER("9012", "请稍后再试", "Please try again later."),
    SYSTEM_MAINTAINED("9013", "系统维护中", "System is maintained."),
    MULTIPLE_LOGIN("9014", "检测到多账号登录", "Multiple account login detected."),
    DATA_NOT_FOUND("9015", "数据不存在", "Data does not exist."),
    INVALID_TOKEN("9016", "无效的令牌", "Invalid token."),
    RATE_LIMIT("9019", "请求频率超出限制", "Request rate limit exceeded."),
    ONE_TIME_TICKET("9020", "每次登录仅能获取一次游戏票", "Every time login only can get one time game ticket."),
    SESSION_VIOLATION("9021", "违反一次会话政策", "Violation of one time session policy."),
    GAME_MAINTAINED("9022", "游戏维护中", "Game is maintained."),
    CURRENCY_UNSUPPORTED("9023", "不支持的币种", "Currency not support."),
    MULTIPLIER_TOO_LOW("9024", "中奖倍数必须大于或等于10倍", "Winning multiplier must be greater than or equal to 10x."),
    REPLAY_NOT_SUPPORTED("9025", "游戏不支持重放", "Game is not support replay."),
    DEMO_NOT_SUPPORTED("9027", "不支持试玩", "Demo is not support."),

    PARAM_ERROR("8000", "输入参数错误，请检查参数是否正确", "The parameter of input error, please check your parameter is correct or not."),
    PARAM_EMPTY("8001", "参数不能为空", "The parameter cannot be empty."),
    PARAM_POSITIVE_INT("8002", "参数必须为正整数", "The parameter must be a positive integer."),
    PARAM_NEGATIVE("8003", "参数不能为负数", "The parameter cannot be negative."),
    SDATE_SECOND_FORMAT_ERROR("8005", "错误的秒数时间格式", "Wrong sdate second format."),
    TIME_NOT_MATCH("8006", "时间不符合要求", "Time does not meet."),
    PARAM_ONLY_NUMBER("8007", "参数只能使用数字", "The parameter only can use number."),
    PARAM_NOT_FOUND("8008", "找不到参数", "The parameter cannot be found."),
    TIME_RANGE_EXCEEDED("8009", "时间区间超出允许范围", "Time interval exceeds the allowable range."),
    PARAM_TOO_LONG("8010", "参数长度过长", "The length of parameter is too long."),
    DATE_MINUTE_FORMAT_ERROR("8013", "错误的分钟时间格式参数", "Wrong date minute format parameter."),
    DECIMAL_TOO_LONG("8014", "参数不能超过指定的小数位数", "The parameter must not exceed specified decimal places."),

    PARENT_NOT_FOUND("7001", "找不到指定的上级ID", "The specified parent ID cannot be found."),
    PARENT_SUSPENDED("7002", "上级被停用", "Parent is suspended."),
    PARENT_LOCKED("7003", "上级被锁定", "Parent is locked."),
    PARENT_CLOSED("7004", "上级已关闭", "Parent is closed."),

    LOGGED_OUT("7405", "您已被登出！", "You have been logged out!"),

    USER_NOT_FOUND("7501", "找不到用户ID", "User ID cannot be found."),
    USER_SUSPENDED("7502", "用户被停用", "User is suspended."),
    USER_LOCKED("7503", "用户被锁定", "User is locked."),
    USER_CLOSED("7504", "用户已关闭", "User is closed."),
    USER_NOT_PLAYING("7505", "用户未在游戏中", "User is not playing."),
    DEMO_ACCOUNT_FULL("7506", "试玩账号已满", "Demo account is full."),

    INVALID_USER_ID("7601", "无效的用户ID，仅允许使用 a-z 和 0-9", "Invalid User ID. Please only use characters between a-z, 0-9."),
    USER_ALREADY_EXISTS("7602", "账号已存在，请使用其他用户ID", "Account already exist. Please choose other User ID."),
    INVALID_USERNAME("7603", "用户名无效", "Invalid username."),
    INVALID_PASSWORD("7604", "密码至少需6位，且包含1个字母和1个数字", "The password must at least 6 characters, with 1 alphabet and 1 number."),
    INVALID_OPERATION_CODE("7605", "无效的操作码，仅允许使用 2,3,4,5", "Invalid operation_code. Please only use number 2, 3, 4, 5."),

    INSUFFICIENT_BALANCE("6001", "余额不足，无法提款", "Your Cash Balance not enough to withdraw."),
    ZERO_BALANCE("6002", "用户余额为零", "User balance is zero."),
    NEGATIVE_WITHDRAW("6003", "提款金额为负数", "Withdraw negative amount."),
    DUPLICATE_TRANSFER("6004", "重复转账", "Duplicate Transfer."),
    DUPLICATE_SERIAL("6005", "重复的流水号", "Repeat serial number."),
    DEPOSIT_LIMIT_EXCEEDED("6009", "存款金额超过上限", "The deposit amount exceeds the upper limit."),
    BALANCE_LIMIT_EXCEEDED("6010", "余额超过上限", "Balance exceeds the upper limit."),
    CREDIT_LIMIT_EXCEEDED("6011", "分配的额度超过上限", "The credit allocated exceeds the upper limit."),
    SERIAL_IN_PROGRESS("6012", "流水号正在处理中", "Serial no is in progress."),
    BET_LIMIT_REACHED("6013", "达到投注限制", "Reach bet limit."),
    USER_PLAYING("6901", "用户正在游戏中，不允许转账余额", "User is playing game, and not allow transfer balance."),

    BALANCE_NOT_ENOUGH("6006", "余额不足", "Your Cash Balance not enough."),
    BET_TRY_AGAIN_LATER("9017", "处理中，请稍后再试", "Please try again later."),
    BET_IS_VALID("6101", "注单成立", "The bet is valid.");
    private  String code;
    private  String messageZh;
    private  String messageEn;

    JDBErrorEnum(String code, String messageZh, String messageEn) {
        this.code = code;
        this.messageZh = messageZh;
        this.messageEn = messageEn;
    }

    public static JDBErrorEnum fromCode(String code) {
        for (JDBErrorEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return FAILED;
    }
}

