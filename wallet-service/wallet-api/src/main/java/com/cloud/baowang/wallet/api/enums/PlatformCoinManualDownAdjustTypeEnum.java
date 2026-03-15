package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param platform_coin_manual_adjust_down_type
 */
@AllArgsConstructor
@Getter
public enum PlatformCoinManualDownAdjustTypeEnum {


    MEMBER_VIP_BENEFITS(1, "会员VIP优惠"),

    PROMOTIONS(2, "会员活动"),

    OTHER(3, "其他调整"),

   ;

    private final Integer code;
    private final String name;


    public static PlatformCoinManualDownAdjustTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        PlatformCoinManualDownAdjustTypeEnum[] types = PlatformCoinManualDownAdjustTypeEnum.values();
        for (PlatformCoinManualDownAdjustTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PlatformCoinManualDownAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
