package com.cloud.baowang.play.api.enums;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;

import java.util.Arrays;
import java.util.List;

public enum SBActionEnum {
    PLACE_BET("PlaceBet", "下注接口"),
    CONFIRM_BET("ConfirmBet", "确认下注"),
    CANCEL_BET("CancelBet", "取消下注"),
    RE_SETTLE("Resettle", "重新结算"),
    SETTLE("Settle", "下注结算"),
    UN_SETTLE("Unsettle", "结算失败-重试结算"),
    PLACE_BET_PARLAY("PlaceBetParlay", "串关下注"),
    CONFIRM_BET_PARLAY("ConfirmBetParlay", "串关下注确认"),
    ;
    private String code;
    private String name;

    SBActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return VenueEnum.SBA.getVenueName()+"-"+name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SBActionEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        SBActionEnum[] types = SBActionEnum.values();
        for (SBActionEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(String code){
        SBActionEnum statusEnum = nameOfCode(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }


    public static List<SBActionEnum> getList() {
        return Arrays.asList(values());
    }

}
