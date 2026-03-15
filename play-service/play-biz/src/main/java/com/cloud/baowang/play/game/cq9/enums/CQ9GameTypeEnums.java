package com.cloud.baowang.play.game.cq9.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 遊戲種類枚举类
 *
 * @author: lavine
 * @creat: 2023/9/12
 */
@Getter
@AllArgsConstructor
public enum CQ9GameTypeEnums {

    SLOT("slot", "老虎機"),
    ARCADE("arcade", "街機"),
    FISH("fish", "漁機"),
    TABLE("table", "牌桌"),
    LIVE("live", "真人"),
    ;

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;

    public static String nameOfCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }

        for (CQ9GameTypeEnums typeEnum : CQ9GameTypeEnums.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }
}
