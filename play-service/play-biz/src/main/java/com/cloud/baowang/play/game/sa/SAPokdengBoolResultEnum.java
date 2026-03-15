package com.cloud.baowang.play.game.sa;


import lombok.Getter;

@Getter
public enum SAPokdengBoolResultEnum {

    Player_Win(1, "Player_Win", "闲赢"),
    Banker_Wins(2, "Banker_Wins", "庄赢"),
    Tie(3, "Tie", "和");


    private final Integer code;
    private final String name;
    private final String desc;

    SAPokdengBoolResultEnum(Integer code,String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }



    public static SAPokdengBoolResultEnum fromCode(Integer code) {
        for (SAPokdengBoolResultEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}
