package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param platform_coin_manual_adjust_way
 */
@Getter
@AllArgsConstructor
public enum PlatformCoinManualAdjustWayEnum {
    PLATFORM_COIN_MANUAL_UP(1, "平台币上分"),
    PLATFORM_COIN_MANUAL_DOWN(2, "平台币下分"),
    ;
    private final Integer code;
    private final String name;

    public static PlatformCoinManualAdjustWayEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        PlatformCoinManualAdjustWayEnum[] types = PlatformCoinManualAdjustWayEnum.values();
        for (PlatformCoinManualAdjustWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PlatformCoinManualAdjustWayEnum> getList() {
        return Arrays.asList(values());
    }
}
