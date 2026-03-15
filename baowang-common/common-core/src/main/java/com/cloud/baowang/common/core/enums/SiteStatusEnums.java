package com.cloud.baowang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SiteStatusEnums {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),
    MAINTENANCE(2, "维护中"),
    ;
    private final Integer status;
    private final String desc;


}
