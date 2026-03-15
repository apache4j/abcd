package com.cloud.baowang.wallet.api.enums.wallet;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum PlatformCoinBalanceTypeEnum {


    INCOME("1", "收入"),
    EXPENSES("2", "支出"),
    ;

    private String code;
    private String name;

    PlatformCoinBalanceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PlatformCoinBalanceTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        PlatformCoinBalanceTypeEnum[] types = PlatformCoinBalanceTypeEnum.values();
        for (PlatformCoinBalanceTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PlatformCoinBalanceTypeEnum> getList() {
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
