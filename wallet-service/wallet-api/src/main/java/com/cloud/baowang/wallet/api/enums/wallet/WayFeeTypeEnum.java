package com.cloud.baowang.wallet.api.enums.wallet;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 *
 * 存提方式手续费类型
 */
public enum WayFeeTypeEnum {

    PERCENTAGE(0, "百分比"),
    FIXED_AMOUNT(1, "固定金额"),
    PERCENTAGE_FIXED_AMOUNT(2, "百分比+固定金额"),
    ;

    private Integer code;
    private String name;

    WayFeeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WayFeeTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        WayFeeTypeEnum[] types = WayFeeTypeEnum.values();
        for (WayFeeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String parseName(String code) {
        if (null == code) {
            return null;
        }
        WayFeeTypeEnum[] types = WayFeeTypeEnum.values();
        for (WayFeeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static List<WayFeeTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
