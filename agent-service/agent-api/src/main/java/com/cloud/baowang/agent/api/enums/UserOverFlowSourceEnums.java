package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserOverFlowSourceEnums {
    AGENT(0, "代理端"),
    SITE_BACKEND(1, "站点后台");
    private final Integer type;
    private final String desc;
}
