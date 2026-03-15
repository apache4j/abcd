package com.cloud.baowang.system.api.enums.exchange;

import java.util.Arrays;
import java.util.List;

/**
 * 货币类型
 */
public enum RateTypeEnum {
    ENCRYPT("ENCRYPT", "加密货币",8),
    CURRENCY("CURRENCY", "法币类型",8),
    ;

    private String code;
    private String name;

    private int decimalLength;

    RateTypeEnum(String code, String name,int decimalLength) {
        this.code = code;
        this.name = name;
        this.decimalLength=decimalLength;
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

    public int getDecimalLength() {
        return decimalLength;
    }

    public void setDecimalLength(int decimalLength) {
        this.decimalLength = decimalLength;
    }

    public static RateTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        RateTypeEnum[] types = RateTypeEnum.values();
        for (RateTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<RateTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
