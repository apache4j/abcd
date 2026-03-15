package com.cloud.baowang.play.api.enums.dbDj;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DbDjTransferEnum {

    BET_DEDUCTION(1, "投注扣款"),
    OTHER_DEDUCTION(8, "其他扣款"),

    ROLLBACK_DEDUCTION(3, "注单回滚扣款"),

    SETTLE_ADD(2, "注单结算加款"),
    OTHER_ADD(9, "其他加款");

    private final Integer code;
    private final String desc;

    DbDjTransferEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举
    public static DbDjTransferEnum fromCode(Integer code) {
        for (DbDjTransferEnum e : values()) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }


}
