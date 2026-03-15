package com.cloud.baowang.play.api.enums.cq9;

import lombok.Getter;

@Getter
public enum CQ9ResultCodeEnums {
    SUCCESS("0", "Success"),
    INSUFFICIENT_BALANCE("1", "Insufficient balance."),
    PLAYER_NOT_FOUND("2", "Player not found."),
    PLAYER_NOT_FOUND_1016("1006", "Player not found."),
    PARAM_NOT_FOUND_1003("1003", " param account not found."),
    PARAM_NOT_FOUND_1004("1004", " event time is wrong format."),
    PARAM_NOT_FOUND_1005("1005", " Insufficient Balance."),
    TOKEN_INVALID("3", "Token invalid."),
    AUTHORIZATION_INVALID("4", "Authorization invalid."),
    BAD_PARAMETERS("5", "Bad parameters."),
    ALREADY_HAS_SAME_ACCOUNT("6", "Already has same account."),
    DATA_NOT_FOUND("8", "Data not found."),
    MTCODE_DUPLICATE("9", "MTCode duplicate."),
    MTCODE_DUPLICATE_1015("1015", "MTCode duplicate."),
    MTCODE_DUPLICATE_2009("2009", "MTCode duplicate."),
    TIME_FORMAT_ERROR("10", "Time format error."),
    QUERY_TIME_OUT_OF_RANGE("11", "Query time is out of range."),
    TIME_ZONE_MUST_BE_UTC4("12", "Time zone must be UTC-4."),
    GAME_NOT_FOUND("13", "Game is not found."),
    INCORRECT_ACCOUNT_OR_PASSWORD("14", "Your account or password is incorrect."),
    INVALID_ACCOUNT_OR_PASSWORD_FORMAT("15", "Account or password must use the following characters: a-z A-Z 0-9 -"),
    CHECK_ACCOUNT_API_ERROR("16", "Check account API error."),
    GAME_UNDER_MAINTENANCE("23", "Game is under maintenance. (Single CQ9 game is under maintenance)"),
    ACCOUNT_TOO_LONG("24", "Account too long."),
    CQ9_GAMING_UNDER_MAINTENANCE("26", "CQ9 Gaming is under maintenance. (All CQ9 games are under maintenance)"),
    CURRENCY_NOT_SUPPORTED("28", "Currency is not supported."),
    NO_DEFAULT_POOL_TYPE("29", "No default pool type."),
    CURRENCY_MISMATCH_AGENT("31", "Currency does not match Agent's currency."),
    TRANSACTION_IN_PROGRESS("33", "Transaction in progress, please check later."),
    FUTURE_TIME_ERROR("35", "StartTime and EndTime are future time."),
    PARAM_AVAILABLE_FOR_GENERAL_AGENT_ONLY("38", "This parameter is available for general agent only."),
    ACCOUNT_DEPOSIT_WITHDRAWAL_FORBIDDEN("39", "Deposit and withdrawal of the account has been forbidden."),
    SOMETHING_WRONG("100", "Something wrong."),
    AUTH_SERVICE_ERROR("101", "Auth service error."),
    USER_SERVICE_ERROR("102", "User service error."),
    TRANSACTION_SERVICE_ERROR("103", "Transaction service error."),
    GAME_MANAGER_SERVICE_ERROR("104", "Game Manager service error."),
    WALLET_SERVICE_ERROR("105", "Wallet service error."),
    TVIEWER_SERVICE_ERROR("106", "Tviewer service error."),
    ORDERVIEW_SERVICE_ERROR("107", "Orderview service error."),
    REPORT_SERVICE_ERROR("108", "Report service error."),
    OWNER_FROZEN("200", "This owner has been frozen."),
    OWNER_DISABLED("201", "This owner has been disabled."),
    SERVER_ERR("1014", "Data not found or status is 1014."),
    PLAYER_DISABLED("202", "This player has been disabled.");

    private final String code;
    private final String message;

    CQ9ResultCodeEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
