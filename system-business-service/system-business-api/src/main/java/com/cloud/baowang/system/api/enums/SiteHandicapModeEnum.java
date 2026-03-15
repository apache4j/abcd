package com.cloud.baowang.system.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SiteHandicapModeEnum {
    Internacional(0, "国际盘"),
    China(1, "大陆盘")
    ;
    private final Integer code;
    private final String name;


}
