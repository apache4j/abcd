package com.cloud.baowang.play.game.db.acelt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DBAceLtResultStatusEnum {

//1：待开奖；2：未中奖；3：已中奖；4：挂起；5：已结算。

    PENDING_DRAW(1, "待开奖"),
    NOT_WINNING(2, "未中奖"),
    WINNING(3, "已中奖"),
    SUSPENDED(4, "挂起"),
    SETTLED(5, "已结算"),
   ;

    /**
     * 编码
     */
    private final int code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 赛事结果类型
     */
    public static String nameOfCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (DBAceLtResultStatusEnum sportTypeEnums : DBAceLtResultStatusEnum.values()) {
            if (sportTypeEnums.getCode() == code) {
                return sportTypeEnums.getDesc();
            }
        }
        return "";
    }


}
