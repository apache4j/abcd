package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSportTransferTypeEnum {

    ADD(1, "加款"),
    DEDUCT(2, "扣款");

    private final Integer code;
    private final String desc;

    DbPanDaSportTransferTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DbPanDaSportTransferTypeEnum fromCode(Integer code) {
        if (code == null) return null;
        for (DbPanDaSportTransferTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
