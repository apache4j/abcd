package com.cloud.baowang.account.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum AccountPlatformCoinBalanceTypeEnum {


    INCOME("1", "收入"),
    EXPENSES("2", "支出"),
    ;

    private String code;
    private String name;

    AccountPlatformCoinBalanceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AccountPlatformCoinBalanceTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AccountPlatformCoinBalanceTypeEnum[] types = AccountPlatformCoinBalanceTypeEnum.values();
        for (AccountPlatformCoinBalanceTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AccountPlatformCoinBalanceTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
