package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportTagEnum {

    BET("bet", "投注(扣款)"),
    CONFIRM("confirm", "注单确认(无账变)"),
    SETTLE("settle", "结算(加款)"),
    SETTLE_ROLLBACK("settleRollBack", "结算回滚(扣款)"),
    CANCEL("cancel", "取消(加款)"),
    CANCEL_ROLLBACK("cancelRollBack", "取消回滚(扣款)"),
    REFUSE("refuse", "拒单(加款)");

    private final String code;
    private final String desc;

    DbPanDaSportTagEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
