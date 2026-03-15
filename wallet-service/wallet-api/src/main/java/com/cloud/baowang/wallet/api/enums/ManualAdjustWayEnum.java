package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param manual_adjust_way
 */
@Getter
@AllArgsConstructor
public enum ManualAdjustWayEnum {
    MANUAL_UP_QUOTA(1, "人工增加额度"),
    MANUAL_DOWN_QUOTA(2, "人工扣除额度"),
    ;
    private final Integer code;
    private final String name;

    public static ManualAdjustWayEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ManualAdjustWayEnum[] types = ManualAdjustWayEnum.values();
        for (ManualAdjustWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ManualAdjustWayEnum> getList() {
        return Arrays.asList(values());
    }
}
