package com.cloud.baowang.play.game.jdb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;


@Getter
@AllArgsConstructor
public enum JDBGameTypeEnums {

    SLOT("0", "老虎机"),
    ARCADE("9", "街机"),
    FISH("7", "捕鱼机"),
    TABLE("18", "棋牌"),
    LOTTERY("12", "电子彩票"),
    GTF_SLOT("66", "老虎机"),
    FC_SLOT("32", "老虎机"),
    AMB_SLOT("50", "老虎机"),
    SWG_SLOT("55", "老虎机"),
    YB_SLOT("58", "老虎机"),
    MANCALA_SLOT("57", "老虎机"),
    ONLYPLAY_SLOT("80", "老虎机"),
    INJOY_SLOT("90", "老虎机"),
    SWGS_SLOT("160", "老虎机"),
    FunkyGames_SLOT("140", "老虎机"),

    GTF_FISH("67", "捕鱼机"),
    FC_FISH("31", "捕鱼机"),
    SWG_FISH("70", "捕鱼机"),
    SWGS_FISH("162", "捕鱼机"),

    INJOY_TABLE("93", "棋牌"),
    FunkyGames_TABLE("142", "棋牌"),

    SPRIBE_ARCADE("22", "街机"),
    FC_ARCADE("30", "街机"),
    SWG_ARCADE("56", "街机"),
    MANCALA_ARCADE("75", "街机"),
    ONLYPLAY_ARCADE("81", "街机"),
    INJOY_ARCADE("92", "街机"),
    Aviatrix_ARCADE("150", "街机"),
    FunkyGames_ARCADE("141", "街机"),

    YB_LOTTERY("60", "宾果"),
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

        for (JDBGameTypeEnums typeEnum : JDBGameTypeEnums.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }
}
