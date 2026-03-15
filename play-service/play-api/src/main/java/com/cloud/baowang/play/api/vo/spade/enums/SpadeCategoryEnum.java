package com.cloud.baowang.play.api.vo.spade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpadeCategoryEnum {

    SM("老虎机"),
    FH("捕鱼机"),
    BN("活动"),


    UNKNOWN("未知类型"),
    ;

    final String desc;

    public static SpadeCategoryEnum fromName(String name) {
        for (SpadeCategoryEnum temp : SpadeCategoryEnum.values()) {
            if (temp.name().equalsIgnoreCase(name)) {
                return temp;
            }
        }
        return SpadeCategoryEnum.UNKNOWN;
    }


}
