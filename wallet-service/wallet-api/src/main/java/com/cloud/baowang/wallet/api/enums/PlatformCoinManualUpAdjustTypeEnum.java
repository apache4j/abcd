package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param platform_coin_manual_adjust_up_type
 */
@AllArgsConstructor
@Getter
public enum PlatformCoinManualUpAdjustTypeEnum {


    MEMBER_DEPOSIT(1, "会员VIP优惠"),

    PROMOTIONS(2, "会员活动"),

    OTHER(3, "其他调整"),
   ;

    private final Integer code;
    private final String name;


    public static PlatformCoinManualUpAdjustTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        PlatformCoinManualUpAdjustTypeEnum[] types = PlatformCoinManualUpAdjustTypeEnum.values();
        for (PlatformCoinManualUpAdjustTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PlatformCoinManualUpAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
