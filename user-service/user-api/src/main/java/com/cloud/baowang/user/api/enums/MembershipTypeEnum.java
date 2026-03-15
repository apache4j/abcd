package com.cloud.baowang.user.api.enums;

import lombok.Getter;

@Getter
public enum MembershipTypeEnum {
    ALL(0,"全部"),
    INSERT_READ(1,"插入已读"),
    SPECIFIC_MEMBER(2,"特定会员");



    private final int code;
    private final String name;

    MembershipTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MembershipTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        MembershipTypeEnum[] types = MembershipTypeEnum.values();
        for (MembershipTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
