package com.cloud.baowang.play.api.enums.order;

import lombok.Getter;

@Getter
public enum OrderShowTypeEnum {
    NORMAL(0, "正常注单"), // 电子 棋牌
    BET_CONTENT(1, "局号投注内容注单"),// 真人
    EVENT(2, "赛事类型注单"), // 电竞 体育 斗鸡
    EVENT_MULTI_GAMEPLAY(3, "赛事类型串关注单"), // 电竞 体育
;
    private Integer code;
    private String desc;

    OrderShowTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
