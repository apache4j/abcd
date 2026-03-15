package com.cloud.baowang.play.api.vo.pt2.enums;

import lombok.Getter;

@Getter
public enum PT2ErrorEnums {
    SUCCESS("0",  "SUCCESS","成功"),
    INTERNAL_ERROR("1","INTERNAL_ERROR","一般系统错误"),
    AUTHENTICATION("2",  "ERR_AUTHENTICATION_FAILED","玩家驗證失敗，無法重試 "),
    ERR_INSUFFICIENT_FUNDS("3","ERR_INSUFFICIENT_FUNDS","餘額不足"),
    ERR_TRANSACTION_DECLINED("4","ERR_TRANSACTION_DECLINED","交易因某原因被拒絕，將不會重試"),
    ERR_REGULATORY_REALITYCHECK("5","ERR_REGULATORY_REALITYCHECK","token过期"),
    ERR_REGULATORY_GENERAL("6","ERR_REGULATORY_GENERAL","一般法规错误"),

    UNKNOWN("999",  "UNKNOWN","未知"),
    ;

    private final String code;
    private final String message;
    private final String desc;

     PT2ErrorEnums(String code , String message, String desc) {
         this.code = code;
         this.message = message;
         this.desc = desc;
     }

    public static PT2ErrorEnums fromCode(String code) {
        for (PT2ErrorEnums e : values()) {
            if (e.code .equals(code) ) {
                return e;
            }
        }
        return null;
    }
}
