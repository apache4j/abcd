package com.cloud.baowang.play.api.enums.sb;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sheldon
 */
@Getter
@AllArgsConstructor
public enum SBLiveEnums {
    LIVE("1", "滚球"),

    NO_LIVE("0", "非滚球"),
    ;

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;

    public static String nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        SBLiveEnums[] types = SBLiveEnums.values();
        for (SBLiveEnums type : types) {
            if (code.equals(type.getCode())) {
                return type.getDesc();
            }
        }
        return null;
    }
}
