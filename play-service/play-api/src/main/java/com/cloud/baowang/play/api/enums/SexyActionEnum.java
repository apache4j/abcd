package com.cloud.baowang.play.api.enums;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum SexyActionEnum {
    GET_BALANCE("getBalance", "获取余额"),
    PLACE_BET("bet", "下注接口"),
    CANCEL_BET("cancelBet", "取消下注"),
    ADJUST_BET("adjustBet", "调整投注"),
    VOID_BET("voidBet", "交易作废"),
    UN_VOID_BET("unvoidBet", "取消交易作废"),
    REFUND("refund", "返还金额"),
    SETTLE("settle", "结账派彩"),
    UN_SETTLE("unsettle", "取消结账派彩"),
    VOID_SETTLE("voidSettle", "结账单转为无效"),
    UN_VOID_SETTLE("unvoidSettle", "取消结账单转为无效"),
    BET_N_SETTLE("betNSettle", "下注并直接结算"),
    CANCEL_BET_N_SETTLE("cancelBetNSettle", "取消(下注并直接结算)"),
    FREE_SPIN("freeSpin", "免费旋转"),
    GIVE("give", "活动派彩"),
    RE_SETTLE("resettle", "重新结账派彩"),
    TIP("tip", "打赏"),
    CANCEL_TIP("cancelTip", "取消打赏"),
    ;
    @Getter
    private String code;
    private String name;

    SexyActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public String getName() {
        return VenueEnum.SEXY.getVenueName()+"-"+name;
    }


    public static SexyActionEnum parseActionType(String actionType) {
        if (null == actionType) {
            return null;
        }
        SexyActionEnum[] types = SexyActionEnum.values();
        for (SexyActionEnum type : types) {
            if (actionType.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(String code){
        SexyActionEnum statusEnum = parseActionType(code);
        if(statusEnum == null){
            return null;
        }
        return statusEnum.getName();
    }

    public static List<SexyActionEnum> getList() {
        return Arrays.asList(values());
    }

}
