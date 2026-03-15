package com.cloud.baowang.play.game.acelt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AceLtBetResultStatusEnums {


    FAIL(-2, "结算失败"),
    MANUAL_CANCEL(-1, "人工取消注单"),
    SYS_CANCEL(0, "未投注"), // 0: 未投注
    NO_SETTLEMENT(1, "未结算"),
    PROCESS(2, "结算中"),
    SETTLEMENT(3, "已结算"),
    USER_CANCELLED(4, "用户撤单"),
    ILLEGALITY(8, "非法注单"),
    INVALID(9, "作废"),
    SYSTEM_CANCELLED(5, "系统撤单"); // 5: 系统撤单

    ;

    /**
     * 编码
     */
    private int code;

    /**
     * 描述
     */
    private String name;

    /**
     * 赛事结果类型
     */
    public static String nameOfCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (AceLtBetResultStatusEnums sportTypeEnums : AceLtBetResultStatusEnums.values()) {
            if (sportTypeEnums.getCode() == code) {
                return sportTypeEnums.getName();
            }
        }
        return "";
    }


}
