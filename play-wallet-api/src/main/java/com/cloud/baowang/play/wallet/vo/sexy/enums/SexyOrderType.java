package com.cloud.baowang.play.wallet.vo.sexy.enums;

public enum SexyOrderType {

    BET("1", "下注"),
    CANCEL_BET("2", "取消下注"),
    VOID("3", "交易作废"),
    SETTLE("4", "结算"),
    CANCEL_PAYOUT("5", "派彩取消"),
    RESEND_PAYOUT("6", "重派彩"),
    TIP("7", "打赏"),
    CANCEL_TIP("8", "取消打赏");

    private final String code;
    private final String description;

    SexyOrderType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 通过 code 获取枚举
     */
    public static SexyOrderType fromCode(String code) {
        for (SexyOrderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }


}

