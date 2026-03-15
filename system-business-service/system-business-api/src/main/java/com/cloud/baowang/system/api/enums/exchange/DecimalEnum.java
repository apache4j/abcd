package com.cloud.baowang.system.api.enums.exchange;

import java.util.Arrays;
import java.util.List;

/**
 * 精度
 * 精度 TWO:2位小数 K:千位
 */
public enum DecimalEnum {
    TWO("TWO", "LOOKUP_10110"),
    K("K", "LOOKUP_10111"),
    ;

    private String code;
    private String name;


    DecimalEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String parseName(String code) {
        if (null == code) {
            return null;
        }
        DecimalEnum[] types = DecimalEnum.values();
        for (DecimalEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
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

    public static DecimalEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        DecimalEnum[] types = DecimalEnum.values();
        for (DecimalEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }



    public static List<DecimalEnum> getList() {
        return Arrays.asList(values());
    }

}
