package com.cloud.baowang.play.api.enums.sb;

public enum SBBetTeamEnum {
    H("h", "主队赢"),
    A("a", "客队赢");

    private final String code;
    private final String name;

    SBBetTeamEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static SBBetTeamEnum of(String code){

        for(SBBetTeamEnum num : SBBetTeamEnum.values()){
            if(num.getCode().equals(code)){
                return num;
            }
        }
        return null;
    }
}
