package com.cloud.baowang.play.api.enums;

import org.apache.commons.lang3.StringUtils;

public enum SBResultCode {
    SUCCESS(0, "成功"),
    DUPLICATE_TRANSACTION(1, "重复的交易"),
    PARAMETER_ERROR(101, "参数错误"),
    ACCOUNT_HAS_BEEN_CLOSED(201, "账户已关闭"),
    ACCOUNT_HAS_BEEN_LOCKED(202, "账户已锁定"),
    ACCOUNT_DOES_NOT_EXIST(203, "账户不存在"),
    ACCOUNT_DEACTIVATED(204, "账户停用"),
    ACCOUNT_EXISTS(205, "账户存在"),
    OPERATOR_ID_DOES_NOT_EXIST(301, "OPERATOR_ID_不存在"),
    INVALID_CURRENCY(302, "无效的货币币别"),
    INVALID_USER_ID(303, "无效的用户_ID"),
    LANGUAGE_NOT_SUPPORTED(304, "语系不支援"),
    INVALID_TOKEN(305, "无效的_TOKEN"),
    INVALID_TIME_ZONE(306, "无效的时区"),
    INVALID_AMOUNT(307, "无效的金额"),
    INVALID_TIME_FORMAT(308, "无效的时间格式"),
    INVALID_TRANSACTION_STATUS(309, "无效的交易状态"),
    INVALID_BETTING_AMOUNT_LIMIT_SETTING(310, "无效的下注金额限制设定"),
    INVALID_VERIFICATION_KEY(311, "无效的验证密钥"),
    INVALID_IP_ADDRESS(312, "无效的_IP_地址"),
    NO_PERMISSION(501, "没有权限"),
    INSUFFICIENT_PLAYER_BALANCE(502, "玩家余额不足"),
    NO_SUCH_BET_FOUND(504, "查无该注单"),
    NO_DATA_FOUND(505, "查无资料"),
    UNABLE_TO_EXECUTE_PLEASE_TRY_AGAIN_LATER(506, "无法执行，请稍后再试一次"),
    THE_BET_IS_NOT_ELIGIBLE_FOR_BONUS(507, "注单不符合_BONUS_资格"),
    PLACEBET_SERIES_CANNOT_BE_RETRIED(508, "PLACEBET_SERIES_CANNOT_BE_RETRIED"),
    DATABASE_ERROR(901, "数据库错误"),
    NETWORK_ERROR(902, "网络错误"),
    SYSTEM_UNDER_MAINTENANCE(903, "系统维修中"),
    REQUEST_TIMEOUT(904, "请求逾时"),
    THE_SYSTEM_IS_BUSY(905, "系统忙碌中"),
    SYSTEM_ERROR(999, "系统错误");
    private final Integer code;

    private final String message;

    SBResultCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return String.valueOf(code);
    }

    public String getMessage() {
        return message;
    }

    public static String of(String status) {
        if (StringUtils.isBlank(status)) {
            return status;
        }

        for (SBResultCode sbResultCode : SBResultCode.values()) {
            if (sbResultCode.getCode().equals(status)) {
                return sbResultCode.getMessage();
            }
        }
        return null;
    }

}
