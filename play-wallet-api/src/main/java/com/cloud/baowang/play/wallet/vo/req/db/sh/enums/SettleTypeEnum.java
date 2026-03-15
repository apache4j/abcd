package com.cloud.baowang.play.wallet.vo.req.db.sh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettleTypeEnum {
    UNKNOWN("UNKNOWN", "未知"),
    PAYOUT("PAYOUT", "正常结算"),
    DISCARD("DISCARD", "跳局结算"),
    CANCEL("CANCEL", "取消局(有可能负数，需要扣款)"),
    REPAYOUT("REPAYOUT", "重算局(有可能负数，需要扣款)");

    private final String code;
    private final String value;

    public static SettleTypeEnum fromCode(String code) {
        for (SettleTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
