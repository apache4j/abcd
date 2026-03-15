package com.cloud.baowang.user.api.enums.notice;

import lombok.Getter;

/**
 *  会员类型 1:全部会员 2:特定会员
 */
@Getter
public enum MemberShipTypeEnum {
    ALL_MEMBER(1, "全部会员"),
    SPECIFIC_MEMBERS(2, "特定会员"),

    ;



    private final int code;
    private final String name;

    MemberShipTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MemberShipTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        MemberShipTypeEnum[] types = MemberShipTypeEnum.values();
        for (MemberShipTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
