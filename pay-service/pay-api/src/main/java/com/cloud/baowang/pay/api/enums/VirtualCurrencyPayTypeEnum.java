package com.cloud.baowang.pay.api.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */

public enum VirtualCurrencyPayTypeEnum {


    RECHARGE("RECHARGE", "充值"),
    WITHDRAW("WITHDRAW", "提现"),
    MINI_RECHARGE("MINI_RECHARGE", "小额充值"),
    RECHARGE_ERROR("RECHARGE_ERROR", "协议选错充值"),
    ;

    private String code;
    private String name;

    VirtualCurrencyPayTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static VirtualCurrencyPayTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        VirtualCurrencyPayTypeEnum[] types = VirtualCurrencyPayTypeEnum.values();
        for (VirtualCurrencyPayTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<VirtualCurrencyPayTypeEnum> getList() {
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
