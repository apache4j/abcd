package com.cloud.baowang.user.api.enums.notice;

import lombok.Getter;

/**
 *  数据库存放的发送消息类型
 */
@Getter
public enum MemberTargetTypeEnum {
    MEMBERS(1, "会员"),

    TERMINAL(2, "终端"),
    AGENTS(4, "代理"),
    BUSINESS(5, "商务"),
  ;



    private final int code;
    private final String name;

    MemberTargetTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MemberTargetTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        MemberTargetTypeEnum[] types = MemberTargetTypeEnum.values();
        for (MemberTargetTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
