package com.cloud.baowang.play.game.nextSpin.enums;

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
public enum NextSpinGameTypeEnums {

    SM("SM", "老虎机"),
    TB("TB", "桌游"),
    AD("AD", "街机"),
    BN("BN", "奖金"),
    FH("FH", "捕鱼")
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

        for (NextSpinGameTypeEnums typeEnum : NextSpinGameTypeEnums.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }
}
