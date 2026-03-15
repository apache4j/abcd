package com.cloud.baowang.wallet.api.enums.wallet;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum VIPAwardEnum {

    UPGRADE_BONUS("0", "升级礼金", "0"),
    WEEK_BONUS("1", "周流水礼金", "1"),
    MONTH_BONUS("2", "月流水礼金", "2"),
    WEEK_SPORT_BONUS("3", "周体育礼金", "3"),
    BRITHDAY_BONUS("4", "生日礼金", "4"),
    ;

    private final String code;
    private final String name;
    private final String accountCoinType;

    public static VIPAwardEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        VIPAwardEnum[] types = VIPAwardEnum.values();
        for (VIPAwardEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<VIPAwardEnum> getList() {
        return Arrays.asList(values());
    }

}
