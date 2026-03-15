package com.cloud.baowang.service.vendor.MTPay.vo;

import lombok.Getter;

/**
 * @author: fangfei
 * @createTime: 2024/10/08 11:43
 * @description: 货币映射
 */
@Getter
public enum CodeEnum {
    VND("VND", "VN"),
    MYR("MYR", "MYS");

    private final String currency;
    private final String country;

    CodeEnum(String currency, String country) {
        this.currency = currency;
        this.country = country;
    }

    public static String getCountryCode(String currency) {
        if (currency == null) {
            return null;
        }
        CodeEnum[] codes = CodeEnum.values();
        for (CodeEnum code : codes) {
            if (currency.equals(code.getCurrency())) {
                return code.getCountry();
            }
        }
        return null;
    }

}
