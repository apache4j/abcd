package com.cloud.baowang.play.api.enums.venue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VenueCurrencyTypeEnum {
    SINGLE_CURRENCY(1, "单币种"),
    MULTI_CURRENCY(2, "多币种");
    private Integer code;

    private String name;
}
