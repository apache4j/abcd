package com.cloud.baowang.user.api.enums.notice;

import lombok.Getter;

/**
 *  特定会员类型 1:vip等级 2:主货币 3:特定会员
 */
@Getter
public enum SpecifyMemberShipTypeEnum {
    VIP_LEVEL_MEMBERS(1, "特定会员-vip等级"),
    MAIN_CURRENCY_MEMBERS(2, "特定会员-主货币"),
    SPECIFIC_MEMBERS(3, "特定会员-指定会员"),
    ;



    private final int code;
    private final String name;

    SpecifyMemberShipTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SpecifyMemberShipTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        SpecifyMemberShipTypeEnum[] types = SpecifyMemberShipTypeEnum.values();
        for (SpecifyMemberShipTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
