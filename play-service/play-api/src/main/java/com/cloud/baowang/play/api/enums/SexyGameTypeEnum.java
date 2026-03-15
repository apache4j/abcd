package com.cloud.baowang.play.api.enums;


import lombok.Getter;

/**
 * gameType 对应sh_game_type
 */
@Getter
public enum SexyGameTypeEnum {
    BAC("MX-LIVE-001", "经典百家乐","2"),
    BAC_2("MX-LIVE-002", "百家乐","1"),
    LH("MX-LIVE-006", "龙虎","4"),
    LP("MX-LIVE-009", "轮盘","15"),
    SBR("MX-LIVE-014", "泰国骰宝","17"),
    YXX("MX-LIVE-015", "泰国鱼虾蟹","21"),
    E_SBR("MX-LIVE-016", "Extra骰宝","17"),
    YSD("MX-LIVE-017", "越南色碟","14"),
    TPD("MX-LIVE-018", "泰国博丁","PokDeng"),
    UNKNOWN("UNKNOWN", "未知游戏","unknown"),
    ;

    private final String code;
    private final String name;
    private final String gameType;

    SexyGameTypeEnum(String code, String name, String gameType) {
        this.code = code;
        this.name = name;
        this.gameType = gameType;
    }


    // 根据 code 查找枚举项
    public static SexyGameTypeEnum getEnumByCode(String code) {
        for (SexyGameTypeEnum gameType : SexyGameTypeEnum.values()) {
            if (gameType.getCode().equals(code) ) {
                return gameType;
            }
        }
        return UNKNOWN;
    }

    public static SexyGameTypeEnum getEnumByGameType(String gameType) {
        for (SexyGameTypeEnum gameEnum : SexyGameTypeEnum.values()) {
            if (gameEnum.gameType.equals(gameType) ) {
                return gameEnum;
            }
        }
        return UNKNOWN;
    }


}
