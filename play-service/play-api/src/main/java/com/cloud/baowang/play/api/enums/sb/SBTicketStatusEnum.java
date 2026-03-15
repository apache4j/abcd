package com.cloud.baowang.play.api.enums.sb;

import lombok.Getter;

@Getter
public enum SBTicketStatusEnum {

    WAITING("waiting", "等待中"),
    RUNNING("running", "进行中"),
    VOID("void", "作废"),
    REFUND("refund", "退款"),
    REJECT("reject", "已取消"),
    LOSE("lose", "输"),
    WON("won", "赢"),
    DRAW("draw", "和局"),
    HALF_WON("half won", "半赢"),
    HALF_LOSE("half lose", "半输");

    private String code;
    private String name;

    SBTicketStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        SBTicketStatusEnum[] types = SBTicketStatusEnum.values();
        for (SBTicketStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
