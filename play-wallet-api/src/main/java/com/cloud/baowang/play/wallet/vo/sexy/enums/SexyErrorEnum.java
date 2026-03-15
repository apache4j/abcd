package com.cloud.baowang.play.wallet.vo.sexy.enums;

public enum SexyErrorEnum {
    SYSTEM_BUSY("9998", "System Busy", "系统繁忙"),
    FAIL("9999", "Fail", "失败"),
    SUCCESS("0000", "Success", "成功"),
    NO_PLATFORM_UNDER_AGENT("11", "Do not have this platform under your agent.", "您的代理底下没有此游戏商"),
    INVALID_USER_ID("1000", "Invalid user Id", "无效的使用者账号"),
    ACCOUNT_EXISTED("1001", "Account existed", "帐号已存在"),
    ACCOUNT_NOT_EXISTS("1002", "Account is not exists", "帐号不存在"),
    INVALID_CURRENCY("1004", "Invalid Currency", "无效的货币"),
    LANGUAGE_NOT_EXISTS("1005", "language is not exists", "语言不存在"),
    PT_SETTING_EMPTY("1006", "PT Setting is empty!", "PT 设定为空"),
    INVALID_PT_SETTING_WITH_PARENT("1007", "Invalid PT setting with parent!", "PT 设定与上线冲突"),
    INVALID_TOKEN("1008", "Invalid token!", "无效的 token"),
    INVALID_TIMEZONE("1009", "Invalid timeZone", "无效时区"),
    INVALID_AMOUNT("1010", "Invalid amount", "无效的数量"),
    INVALID_TXCODE("1011", "Invalid txCode", "无效的交易代码"),
    HAS_PENDING_TRANSFER("1012", "Has Pending Transfer", "有待处理的转帐"),
    ACCOUNT_LOCK("1013", "Account is Lock", "帐号已锁"),
    ACCOUNT_SUSPEND("1014", "Account is Suspend", "帐号暂停"),
    TXCODE_ALREADY_OPERATION("1016", "TxCode already operation!", "交易代码已被执行过"),
    TXCODE_NOT_EXIST("1017", "TxCode is not exist", "交易代码不存在"),
    NOT_ENOUGH_BALANCE("1018", "Not Enough Balance", "余额不足"),
    NO_DATA("1019", "No Data", "没有资料"),
    INVALID_DATE_TIME_FORMAT("1024", "Invalid date time format", "无效的日期 (时间) 格式"),
    INVALID_TRANSACTION_STATUS("1025", "Invalid transaction status", "无效的交易状态"),
    INVALID_BET_LIMIT_SETTING("1026", "Invalid bet limit setting", "无效的投注限制设定"),
    INVALID_CERTIFICATE("1027", "Invalid Certificate", "无效的认证码"),
    UNABLE_TO_PROCEED("1028", "Unable to proceed. please try again later.", "无法执行指定的行为，请稍后再试"),
    INVALID_IP_ADDRESS("1029", "invalid IP address.", "无效的 IP"),
    INVALID_DEVICE("1030", "invalid Device to call API.(Ex.IE)", "使用无效的装置呼叫 (例如：IE)"),
    SYSTEM_MAINTENANCE("1031", "System is under maintenance.", "系统维护中"),
    DUPLICATE_LOGIN("1032", "Duplicate login.", "重复登入"),
    INVALID_GAME("1033", "Invalid Game", "无效的游戏"),
    TIME_NOT_MEET("1034", "Time does not meet.", "您使用的时间参数不符合此 API 规定格式"),
    INVALID_AGENT_ID("1035", "Invalid Agent Id.", "无效的 Agent Id"),
    INVALID_PARAMETERS("1036", "Invalid parameters.", "无效的参数"),
    INVALID_CUSTOMER_SETTING("1037", "Invalid customer setting.", "错误的客户设定"),
    DUPLICATE_TRANSACTION("1038", "Duplicate transaction.", "重复的交易"),
    TRANSACTION_NOT_FOUND("1039", "Transaction not found.", "无此交易"),
    REQUEST_TIMEOUT("1040", "Request timeout.", "请求逾时"),
    HTTP_STATUS_ERROR("1041", "HTTP Status error.", "HTTP 状态错误"),
    HTTP_RESPONSE_EMPTY("1042", "HTTP Response is empty.", "HTTP 请求空白"),
    BET_CANCELED("1043", "Bet has canceled.", "下注已被取消"),
    INVALID_BET("1044", "Invalid bet.", "无效的下注"),
    ADD_ACCOUNT_STATEMENT_FAILED("1045", "Add account statement failed.", "帐便记录新增失败"),
    TRANSFER_FAILED("1046", "Transfer Failed! Please contact customer support immediately.", "转帐失败！请立即联系客服"),
    GAME_MAINTENANCE("1047", "Game is under maintenance.", "游戏维护中"),
    INVALID_USERNAME("1050", "Invalid user name", "无效的user name"),
    INVALID_PLATFORM("1054", "Invalid Platform", "无效的平台商"),
    PARAMETER_NOT_FOUND("1056", "[any parameter]not found", "[任一参数]找不到");

    private final String code;
    private final String messageEn;
    private final String messageZh;

    SexyErrorEnum(String code, String messageEn, String messageZh) {
        this.code = code;
        this.messageEn = messageEn;
        this.messageZh = messageZh;
    }

    public String getCode() {
        return code;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public String getMessageZh() {
        return messageZh;
    }

    public static SexyErrorEnum fromCode(String code) {
        for (SexyErrorEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}

