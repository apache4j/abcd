package com.cloud.baowang.system.api.enums;

import lombok.Getter;

/**
 * 站点模式枚举类-对应system_param site_model code
 */
@Getter
public enum SiteModelEnum {
    FULL_PACKAGE(0,"全包"),
    RISK_MANAGEMENT_PACKAGE(1,"包风控"),
    FINANCIAL_PACKAGE(2,"包财务"),
    EXCLUDED_PACKAGE(3,"不包"),
    ;

    private final Integer type;
    private final String name;

    SiteModelEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }


}
