package com.cloud.baowang.play.game.db.sh.enums;

import lombok.Getter;

@Getter
public enum DBSHGameTypeEnum {

    GAME_2001("2001", "经典百家乐","2"),
    GAME_2002("2002", "极速百家乐","1"),
    GAME_2003("2003", "竞咪百家乐","11"),
    GAME_2004("2004", "包桌百家乐","2"),
    GAME_2005("2005", "共咪百家乐","10"),
    GAME_2006("2006", "龙虎","4"),
    GAME_2007("2007", "轮盘","15"),
    GAME_2008("2008", "骰宝","17"),
    GAME_2009("2009", "牛牛","3"),
    GAME_2010("2010", "炸金花","6"),
    GAME_2011("2011", "三公","5"),
    GAME_2013("2013", "多台（不支持gameTypeId 直接进入","23"),
    GAME_2014("2014", "高额贵宾百家乐","9"),
    GAME_2015("2015", "斗牛","3"),
    GAME_2016("2016", "保险百家乐","2"),
    GAME_2019("2019", "德州扑克","8"),
    GAME_2020("2020", "番摊","16"),
    GAME_2021("2021", "21点","18"),
    GAME_2022("2022", "色碟","14"),
    GAME_2023("2023", "温州牌九",""),
    GAME_2025("2025", "安达巴哈","19"),
    GAME_2026("2026", "印度炸金花","7"),
    GAME_2027("2027", "劲舞百家乐（游戏玩法同百家乐）","13"),
    GAME_2029("2029", "六合彩","24"),
    GAME_2030("2030", "主播百家乐（游戏玩法同百家乐）","13"),
    GAME_2031("2031", "3D","25"),
    GAME_2032("2032", "5D","26"),
    GAME_2034("2034", "闪电百家乐","2"),
    GAME_2036("2036", "多利","27"),
    GAME_2038("2038", "电投百家乐","2"),
    UNKNOWN("0", "","");
    private final String code;
    private final String desc;
    private final String gameType;

    DBSHGameTypeEnum(String code, String desc,String gameType) {
        this.code = code;
        this.desc = desc;
        this.gameType = gameType;
    }

    public static DBSHGameTypeEnum fromCode(String code) {
        for (DBSHGameTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
