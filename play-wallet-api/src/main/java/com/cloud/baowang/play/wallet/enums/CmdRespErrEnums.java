package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum CmdRespErrEnums {
    // 成功
    SUCCESS(100, "成功"),

    MEMBER_NOT_EXIST(1001, "会员不存在"),
    GAME_MAINTAINED(1002, "游戏维护中"),
    METHON_NOT_FOUND(1003, "方法名不存在"),
    ACCOUNT_LOCKED(1004, "玩家锁定"),
    VENUE_CLOSE(1005, "场馆关闭"),
    DUPLICATE_ID_NO(1006, "重复交易id"),
    BETORDERNO_NOT_HAVA(1007, "注单不存在"),
    INSUFFICIENT_BALANCE(-9001, "Insuﬃcient Balance"),
    ;

    private final Integer code;
    private final String description;

    CmdRespErrEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

}
