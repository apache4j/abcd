package com.cloud.baowang.system.api.enums.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DictConfigCategoryEnum {
    FIXED(0, "固定值"),
    PERCENTAGE(1, "百分比");

    private final int value;
    private final String description;

}
