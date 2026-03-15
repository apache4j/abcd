package com.cloud.baowang.play.api.enums.dbPanDaSport;

import lombok.Getter;

@Getter
public enum DbPanDaSuccessTypeEnum {

    FAIL("0", "失败"),
    SUCCESS("1", "成功");

    private final String code;
    private final String desc;

    DbPanDaSuccessTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DbPanDaSuccessTypeEnum fromCode(String code) {
        for (DbPanDaSuccessTypeEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
