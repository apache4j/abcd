package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 三方平台转帐状态的枚举类
 *
 * @author: sheldon
 */
@Getter
@AllArgsConstructor
public enum SBATransferEnums {
    PLACE_BET(0, "单关下注,串关下注"),
    CONFIRM_BET(1, "关确认下注,串关确认下注"),
    CANCEL_BET(2, "取消下注"),
    RE_SETTLE(3, "重新结算"),
    SETTLE(4, "下注结算"),
    UN_SETTLE(5, "结算失败-重试结算");

    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        SBATransferEnums[] types = SBATransferEnums.values();
        for (SBATransferEnums type : types) {
            if (code.equals(type.getCode())) {
                return type.getDesc();
            }
        }
        return null;
    }


    public static SBATransferEnums of(Integer code) {
        if (null == code) {
            return null;
        }
        SBATransferEnums[] types = SBATransferEnums.values();
        for (SBATransferEnums type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


}
