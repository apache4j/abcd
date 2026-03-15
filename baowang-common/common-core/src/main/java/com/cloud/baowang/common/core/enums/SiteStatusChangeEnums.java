package com.cloud.baowang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SiteStatusChangeEnums {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),
    MAINTENANCE(2, "维护"),
    ;
    private final Integer status;
    private final String desc;

    public static SiteStatusChangeEnums getSiteStatusEnums(Integer status) {
        if (null == status) {
            return null;
        }
        SiteStatusChangeEnums[] types = SiteStatusChangeEnums.values();
        for (SiteStatusChangeEnums type : types) {
            if (status.equals(type.getStatus())) {
                return type;
            }
        }
        return null;
    }
}
