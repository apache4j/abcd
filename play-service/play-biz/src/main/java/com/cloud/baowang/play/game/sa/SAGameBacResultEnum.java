package com.cloud.baowang.play.game.sa;

import lombok.Getter;

@Getter
public enum SAGameBacResultEnum {

    BRTie("BRTie", "和", true),
    BRPlayerWin("BRPlayerWin", "闲", true),
    BRBankerWin("BRBankerWin", "庄", true),
    BRPlayerPair("BRPlayerPair", "闲对", true),
    BRBankerPair("BRBankerPair", "庄对", true),
    BRSPerfectPair("BRSPerfectPair", "完美对子", true),
    BRSAnyPair("BRSAnyPair", "任意对子", true),
    BRSPlayerBonus("BRSPlayerBonus", "闲龙宝", true),
    BRSBankerBonus("BRSBankerBonus", "庄龙宝", true),
    BRSLuckySix("BRSLuckySix", "幸运六", true),
    BRS2CardsLuckySix("BRS2CardsLuckySix", "两张牌幸运六", true),
    BRS3CardsLuckySix("BRS3CardsLuckySix", "三张牌幸运六", true),

    BRSSTie("BRSSTie", "免水和", true),
    BRSSPlayerWin("BRSSPlayerWin", "免水闲", true),
    BRSSBankerWin("BRSSBankerWin", "免水庄", true),
    BRSSPlayerPair("BRSSPlayerPair", "免水闲对", true),
    BRSSBankerPair("BRSSBankerPair", "免水庄对", true),
    BRSSSPerfectPair("BRSSSPerfectPair", "免水完美对子", true),
    BRSSSAnyPair("BRSSSAnyPair", "免水任意对子", true),
    BRSSSPlayerBonus("BRSSSPlayerBonus", "免水闲龙宝", true),
    BRSSSBankerBonus("BRSSSBankerBonus", "免水庄龙宝", true),
    BRSSSLuckySix("BRSSSLuckySix", "免水幸运六", true),
    BRSSS2CardsLuckySix("BRSSS2CardsLuckySix", "免水两张牌幸运六", true),
    BRSSS3CardsLuckySix("BRSSS3CardsLuckySix", "免水三张牌幸运六", true),

    BRPlayerNatural("BRPlayerNatural", "闲例牌", true),
    BRBankerNatural("BRBankerNatural", "庄例牌", true),
    BRSSPlayerNatural("BRSSPlayerNatural", "免水闲例牌", true),
    BRSSBankerNatural("BRSSBankerNatural", "免水庄例牌", true);

    private final String code;
    private final String name;
    private final boolean boolType;

    SAGameBacResultEnum(String code, String name, boolean boolType) {
        this.code = code;
        this.name = name;
        this.boolType = boolType;
    }
}
