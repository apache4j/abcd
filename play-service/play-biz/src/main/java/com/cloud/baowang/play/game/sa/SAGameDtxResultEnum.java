package com.cloud.baowang.play.game.sa;

import lombok.Getter;

@Getter
public enum SAGameDtxResultEnum {

    DTR_TIE("DTRTie", "和", true),
    DTR_DRAGON_WIN("DTRDragonWin", "龙", true),
    DTR_TIGER_WIN("DTRTigerWin", "虎", true);

    private final String code;
    private final String name;
    private final boolean flag;

    SAGameDtxResultEnum(String code, String name, boolean flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

}
