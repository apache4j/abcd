package com.cloud.baowang.play.game.cmd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sheldon
 */
@Getter
@AllArgsConstructor
public enum CmdLiveEnums {
    LIVE(true, "滚球","LIVE"),

    NO_LIVE(false, "非滚球","NO_LIVE"),
    ;

    /**
     * 编码
     */
    private Boolean code;

    /**
     * 描述
     */
    private String desc;

    /**
     * 描述
     */
    private String descEn;


    public static CmdLiveEnums nameOfCode(Boolean code) {
        if (null == code) {
            return null;
        }
        CmdLiveEnums[] types = CmdLiveEnums.values();
        for (CmdLiveEnums type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
