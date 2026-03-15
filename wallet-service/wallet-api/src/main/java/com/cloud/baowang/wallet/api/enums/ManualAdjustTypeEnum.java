package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * system_param manual_adjust_type
 * 会员人工加额类型枚举
 */
@AllArgsConstructor
@Getter
public enum ManualAdjustTypeEnum {

    OTHER(3, "其他调整"),

    MEMBER_DEPOSIT(4, "会员存款(后台)"),

    MEMBER_VIP_BENEFITS(5, "会员VIP优惠"),

    PROMOTIONS(6, "会员活动"),

    REPAIR_MISSING_LIMIT(7, "补单-缺失额度"),
    TEST_UP(8, "测试上分"),
    REPAIR_OTHERS(9, "补单-其他调整"),
    MEMBER_REBATE(10, "会员返水"),

    RISK_CONTROL_ADJUSTMENT(11, "风控调整"),
    ;



    private final Integer code;
    private final String name;


    public static ManualAdjustTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ManualAdjustTypeEnum[] types = ManualAdjustTypeEnum.values();
        for (ManualAdjustTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ManualAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
