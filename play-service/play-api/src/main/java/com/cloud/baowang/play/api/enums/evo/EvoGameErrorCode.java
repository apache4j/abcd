package com.cloud.baowang.play.api.enums.evo;


/**
 * Evo 游戏错误码枚举
 */
public enum EvoGameErrorCode {

    /*** 成功，无错误 */
    OK("OK", "Success", "OK", "Transaction request is processed successfully.", "All"),

    /*** 临时错误，可重试，玩家下注被拒绝，玩家留在游戏中 */
    TEMPORARY_ERROR("2000", "TEMPORARY_ERROR", "TEMPORARY_ERROR",
            "Player’s bet rejected, Player remains in the game", "All"),

    /*** 会话过期，致命错误，玩家需要重新登录 */
    INVALID_TOKEN_ID("10003", "Fatal Error", "INVALID_TOKEN_ID",
            "Player’s bet rejected, Player required to launch games again", "All"),

    /*** 账户被锁定，致命错误，玩家需要退出游戏 */
    ACCOUNT_LOCKED("10007", "Fatal Error", "ACCOUNT_LOCKED",
            "Player’s bet rejected, Player required to quit the games", "All"),

    /*** 投注超限，致命错误，玩家需要关闭并重新打开游戏 */
    FATAL_ERROR_CLOSE_USER_SESSION("3004", "Fatal Error", "FATAL_ERROR_CLOSE_USER_SESSION",
            "Player’s bet rejected, Player gets an error screen", "All"),

    /*** 未知错误，玩家下注被拒绝 */
    UNKNOWN_ERROR("1049", "Final Error", "UNKNOWN_ERROR",
            "Player’s bet rejected, Player remains in the game", "All"),

    /*** 参数无效 */
    INVALID_PARAMETER("10002", "Final Error", "INVALID_PARAMETER",
            "Player’s bet rejected, Player remains in the game", "All"),

    /*** 下注不存在 */
    BET_DOES_NOT_EXIST("10005", "Final Error", "BET_DOES_NOT_EXIST",
            "Player’s bet rejected, Player remains in the game", "Credit, Cancel"),

    /*** 下注已存在 */
    BET_ALREADY_EXIST(null, "Success", "BET_ALREADY_EXIST", "Bet already exists in third party system", "Debit"),

    /*** 下注已结算 */
    BET_ALREADY_SETTLED(null, "Success", "BET_ALREADY_SETTLED", "Bet has been already settled in third party system", "Credit, Cancel"),

    /*** 余额不足，下注失败 */
    INSUFFICIENT_FUNDS("10008", "Final Error", "INSUFFICIENT_FUNDS",
            "Player’s bet rejected, Player remains in the game", "Debit"),

    /*** 余额不足，打赏失败 */
    INSUFFICIENT_FUNDS_TIPS("10011", "Final Error", "INSUFFICIENT_FUNDS_TIPS",
            "Player’s tip transaction rejected, Player remains in the game", "Withdraw Tip"),

    /*** 动作失败，致命错误，玩家下注被拒绝 */
    FINAL_ERROR_ACTION_FAILED("2001", "Final Error", "FINAL_ERROR_ACTION_FAILED",
            "The attempted action failed. Please try again",
            "All"),

    /*** 地理位置检查失败，致命错误，玩家下注被拒绝 */
    GEOLOCATION_FAIL("1100", "Final Error", "GEOLOCATION_FAIL",
            "Operation not allowed because of geolocation check.",
            "All"),

    /*** 奖金超限，致命错误，玩家下注被拒绝 */
    BONUS_LIMIT_EXCEEDED("1051", "Final Error", "BONUS_LIMIT_EXCEEDED",
            "Bet rejected. Your stake exceeds the maximum allowed with this bonus. Please lower your stake and try again",
            "Debit");

    private final String code;
    private final String type;
    private final String message;
    private final String gameBehavior;
    private final String applicableMethod;

    EvoGameErrorCode(String code, String type, String message, String gameBehavior, String applicableMethod) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.gameBehavior = gameBehavior;
        this.applicableMethod = applicableMethod;
    }

    public static EvoGameErrorCode fromCode(String code) {
        for (EvoGameErrorCode e : values()) {
            if (e.code != null && e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getGameBehavior() {
        return gameBehavior;
    }

    public String getApplicableMethod() {
        return applicableMethod;
    }
}
