package com.cloud.baowang.play.api.enums;

import java.util.Arrays;
import java.util.List;

public enum LobbyOrderRecordTypeEnum {
    NEW_BET(1, "最新投注"),
    BIG_BET(2, "大额投注"),
    BET_CONTEST(3, "投注比赛"),
    ;
    private Integer code;
    private String name;

    LobbyOrderRecordTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LobbyOrderRecordTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        LobbyOrderRecordTypeEnum[] types = LobbyOrderRecordTypeEnum.values();
        for (LobbyOrderRecordTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code){
        LobbyOrderRecordTypeEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<LobbyOrderRecordTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
