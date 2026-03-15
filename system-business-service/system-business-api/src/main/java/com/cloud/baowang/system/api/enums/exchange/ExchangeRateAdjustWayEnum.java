package com.cloud.baowang.system.api.enums.exchange;

import java.util.Arrays;
import java.util.List;

/**
 * 调整方式
 * 关联到 system_param 重的 exchange_rate_adjust_way
 */
public enum ExchangeRateAdjustWayEnum {
    PERCENTAGE(1, "百分比调整"),
    FIXED_VALUE(2, "固定值调整"),
    ;

    private Integer code;
    private String name;

    ExchangeRateAdjustWayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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

    public static ExchangeRateAdjustWayEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ExchangeRateAdjustWayEnum[] types = ExchangeRateAdjustWayEnum.values();
        for (ExchangeRateAdjustWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return null;
        }
        ExchangeRateAdjustWayEnum[] types = ExchangeRateAdjustWayEnum.values();
        for (ExchangeRateAdjustWayEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }


    public static List<ExchangeRateAdjustWayEnum> getList() {
        return Arrays.asList(values());
    }

}
