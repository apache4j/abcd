package com.cloud.baowang.user.api.enums.notice;

import lombok.Getter;

/**
 *  数据库存放的发送消息类型
 */
@Getter
public enum TargetTypeEnum {
    ALL_MEMBERS(1, "全体会员"),
    VIP_LEVEL_MEMBERS(2, "特定会员-vip等级"),
    MAIN_CURRENCY_MEMBERS(3, "特定会员-主货币"),
    SPECIFIC_MEMBERS(4, "特定会员-指定会员"),
    TERMINAL(5, "终端"),
    ALL_AGENTS(6, "全部代理"),
    SPECIFIC_AGENTS(7, "特定代理");



    private final int code;
    private final String name;

    TargetTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TargetTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TargetTypeEnum[] types = TargetTypeEnum.values();
        for (TargetTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
