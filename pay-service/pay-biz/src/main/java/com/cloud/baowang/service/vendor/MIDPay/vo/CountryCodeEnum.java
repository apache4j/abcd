package com.cloud.baowang.service.vendor.MIDPay.vo;


/**
 * @author: fangfei
 * @createTime: 2024/10/17 22:48
 * @description:
 */
public enum CountryCodeEnum {
    MYR("MYR", "MY"),
    VND("VND", "VN")
    ;

    private String currencyCode;
    private String countryCode;

    CountryCodeEnum(String currencyCode, String countryCode) {
        this.currencyCode = currencyCode;
        this.countryCode = countryCode;
    }

    public static String getCountryCode(String currencyCode) {
        if (null == currencyCode) {
            return null;
        }
        CountryCodeEnum[] types = CountryCodeEnum.values();
        for (CountryCodeEnum type : types) {
            if (currencyCode.equals(type.getCurrencyCode())) {
                return type.getCountryCode();
            }
        }
        return null;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
