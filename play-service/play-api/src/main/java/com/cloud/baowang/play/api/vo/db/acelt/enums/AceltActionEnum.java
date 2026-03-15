package com.cloud.baowang.play.api.vo.db.acelt.enums;

public enum AceltActionEnum {
    UNKNOWN("0", "未知"),
    BETTING("1", "投注（扣款）"),
    CANCEL_ORDER("2", "撤单（加款）"),
    PAYOUT("3", "派奖（加款）"),
    CANCEL_PAYOUT("4", "撤回派奖（扣款）"),
    SECOND_PAYOUT("5", "二次派奖（加款）"),
    BETTING_REBATE("6", "投注返点（加款）"),
    CANCEL_BETTING_REBATE("7", "撤回投注返点（扣款）");

    private final String code;
    private final String desc;

    AceltActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AceltActionEnum fromCode(String code) {
        for (AceltActionEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}

