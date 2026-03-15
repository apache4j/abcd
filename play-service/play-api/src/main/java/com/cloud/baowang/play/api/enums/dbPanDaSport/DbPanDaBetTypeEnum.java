package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaBetTypeEnum {


    UN_CONFIRMED(0, "未确认"),
    CONFIRMED(1, "已确认"),
    CANCELED(2, "已取消");

    private final Integer code;
    private final String desc;

    DbPanDaBetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static DbPanDaBetTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (DbPanDaBetTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
