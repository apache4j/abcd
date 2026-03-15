package com.cloud.baowang.system.api.enums.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * system_param banner_area code
 */
@Getter
@AllArgsConstructor
public enum BannerArea {
    AREA_1(0, "1区"),
    AREA_2(1, "2区"),
    AREA_3(2, "3区"),
    AREA_4(3, "4区"),
    AREA_5(4, "5区"),
    AREA_6(5, "6区"),
    AREA_7(6, "7区"),
    AREA_8(7, "8区"),
    AREA_9(8, "9区"),
    AREA_10(9, "10区");


    private final int code;
    private final String description;
}
