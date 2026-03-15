package com.cloud.baowang.play.game.fastSpin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FSCategoryEnum {

    SM("老虎机"),
    FH("捕鱼机"),
    BN("活动"),


    UNKNOWN("未知类型"),
    ;

    final String desc;

    public static FSCategoryEnum fromName(String name) {
        for (FSCategoryEnum temp : FSCategoryEnum.values()) {
            if (temp.name().equalsIgnoreCase(name)) {
                return temp;
            }
        }
        return FSCategoryEnum.UNKNOWN;
    }


}
