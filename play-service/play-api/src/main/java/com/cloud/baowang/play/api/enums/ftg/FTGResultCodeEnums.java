package com.cloud.baowang.play.api.enums.ftg;

import lombok.Getter;

@Getter
public enum FTGResultCodeEnums {
    UNKNOWN("99-000", "当返回的 error_code 非 FunTa 定义的 wrongStatusCode error_code 时，系统会返回此错误。"),
    INVALID_PARTNER("99-001", "运营商已经被关闭或是传入不正确的 client_id 会返回此参数。"),
    INVALID_TOKEN("99-002", "运营商无法识别 token。此错误与 Token 到期为不同的错误。"),
    INVALID_GAME("99-003", "运营商传入的游戏不存在或是已关闭。"),
    WRONG_CURRENCY("99-004", "币别不是用户钱包所使用的币别。"),
    NOT_ENOUGH_BALANCE("99-005", "余额不足。"),
    USER_DISABLE("99-006", "用户被停押 / 注销。"),
    INVALID_SIGNATURE("99-007", "运营商没有依照 2.3. API 编码说明代入验证。"),
    TOKEN_EXPIRED("99-008", "运营商 Token 已到期。"),
    WRONG_SYNTAX("99-009", "请求格式与定义的 API 接口不符。"),
    WRONG_TYPES("99-010", "参数非预期的格式。"),
    DUPLICATE_TRANSACTION("99-011", "transaction_uuid 重复，但传入的金额、currency、bet_at、uid 或 游戏代码不相同。"),
    TRANSACTION_DOES_NOT_EXIST("99-012", "当 FunTa 向运营商请求交易：派彩 时，运营商无法在系统中找到对应的 reference_transaction_uuid，需返回此错误信息。"),
    TRANSACTION_ROLLED_BACK("99-013", "FunTa 向运营商第二次请求交易：回滚 且代入的参数两次不一致时，需返回此错误信息。"),
    TRANSACTION_HAS_FINISHED("99-014", "该 transaction 已完成，但重复收到交易请求。");
    private final String code;
    private final String message;

    FTGResultCodeEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
