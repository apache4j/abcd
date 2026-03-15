package com.cloud.baowang.account.api.enums;

import lombok.Getter;

@Getter
public enum BalanceTypeEnums {

    INCOME("1", "收入"),
    EXPENSES("2", "支出"),
    ;


    private final String type;
    private final String value;

    // 构造函数
    BalanceTypeEnums(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
