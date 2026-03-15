package com.cloud.baowang.wallet.api.enums.wallet;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum VIPAwardV2Enum {

    UPGRADE_BONUS("0", "升级礼金","0"),
    WEEK_BONUS("1", "周流水礼金","1"),
    //MONTH_BONUS("2", "月流水礼金"),
    BIRTH_BONUS("4", "年礼金（生日）","4"),
    ;

    private final String code;
    private final String name;
    private final String accountCoinType;

    public static VIPAwardV2Enum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        VIPAwardV2Enum[] types = VIPAwardV2Enum.values();
        for (VIPAwardV2Enum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<VIPAwardV2Enum> getList() {
        return Arrays.asList(values());
    }

    public static String getAwardOrderCode(VIPAwardV2Enum vipAwardV2Enum, String month, String week, String grade) {
        return vipAwardV2Enum.name + "_"+ month + "_"+ week + "_"+ grade;
    }
}
