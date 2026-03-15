package com.cloud.baowang.account.api.enums;

import lombok.Getter;

@Getter
public enum AccountTypeEnums {
    DEBIT("0", "借记账户"),
    CREDIT("1", "贷记账户"),
    ;
    private final String type;
    private final String value;

    // 构造函数
    AccountTypeEnums(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static AccountTypeEnums of(String type) {
        for (AccountTypeEnums accountTypeEnums : AccountTypeEnums.values()) {
            if (accountTypeEnums.type.equals(type) ) {
                return accountTypeEnums;
            }
        }
        return null; // 异常
    }
}
