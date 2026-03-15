package com.cloud.baowang.account.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 * system_param中的
 * system_param 中的  coin_balance_type
 *
 */

public enum AccountBalanceTypeEnum {


    INCOME("1", "收入"),
    EXPENSES("2", "支出"),
    FREEZE("3", "冻结"),
    UN_FREEZE("4", "解冻"),
    ;

    private String code;
    private String name;

    AccountBalanceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AccountBalanceTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AccountBalanceTypeEnum[] types = AccountBalanceTypeEnum.values();
        for (AccountBalanceTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AccountBalanceTypeEnum> getList() {
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
