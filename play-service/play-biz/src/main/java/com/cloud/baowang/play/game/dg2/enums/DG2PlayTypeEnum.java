package com.cloud.baowang.play.game.dg2.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum DG2PlayTypeEnum {
    BANKER("banker", "庄","BJL_BANKER_COMM"),
    BANKER6("banker6", "免佣庄","BJL_BANKER_NO_COMM"),
    PLAYER("player", "闲","BJL_PLAYER"),
    TIE("tie", "和","BJL_TIE"),
    B_PAIR("bPair", "庄对","BJL_BANKER_PAIR"),
    P_PAIR("pPair", "闲对","BJL_PLAYER_PAIR"),
    BIG("big", "大","CD_BIG"),
    SMALL("small", "小","CD_SMALL"),
    B_BX("bBX", "庄保险","BANKER_INSURANCE"),
    P_BX("pBX", "闲保险","PLAYER_INSURANCE"),
    SUPER6("super6", "幸运六","BJL_SUPER_SIX"),
    ANY_PAIR("anyPair", "任意对子","BJL_EITHER_PAIR"),
    PERFECT_PAIR("perfectPair", "完美对子","BJL_PERFECT_PAIR"),
    B_BONUS("bBonus", "庄龙宝","BJL_BANKER_DRAGON"),
    P_BONUS("pBonus", "闲龙宝","BJL_BANKER_DRAGON"),
    PANDA8("panda8", "熊猫8","BJL_PANDA_8"),
    DRAGON7("dragon7", "龙7","BJL_DRAGON_7"),
    TIGER_TIE("tigerTie", "老虎和","BJL_TIGER_TIE"),
    BIG_TIGER("bigTiger", "大老虎","BJL_TIGER_BIG"),
    SMALL_TIGER("smallTiger", "小老虎","BJL_TIGER_SMALL"),
    TIGER_PAIR("tigerPair", "老虎对","BJL_TIGER_PAIR"),
    P_NAT("pNat", "闲天牌","BJL_PLAYER_SKY_CARD"),
    NATURAL("natural", "天牌","BJL_SKY_CARD"),
    B_NAT("bNat", "庄天牌","BJL_BANKER_SKY_CARD"),

    TIGER("tiger", "虎","BJL_TIGER"),
    DT_TIE("dtTie", "龙虎和","BJL_DRAGON_TIGER_TIE"),
    DRAGON("dragon", "龙","BJL_DRAGON"),
    SUP_TIE("supTie", "超和","BJL_SUPER_TIE_6"),

    //龙虎 TODO
    DRAGON_RED("dragonRed", "龙红", "LH_DRAGON_RED"),
    DRAGON_BLACK("dragonBlack", "龙黑", "LH_DRAGON_BLACK"),
    TIGER_RED("tigerRed", "虎红", "LH_TIGER_RED"),
    TIGER_BLACK("tigerBlack", "虎黑", "LH_TIGER_BLACK"),
    DRAGON_ODD("dragonOdd", "龙单", "LH_DRAGON_ODD"),
    TIGER_ODD("tigerOdd", "虎单", "LH_TIGER_ODD"),
    DRAGON_EVEN("dragonEven", "龙双", "LH_DRAGON_EVEN"),
    TIGER_EVEN("tigerEven", "虎双", "LH_TIGER_EVEN"),


    // --- 轮盘 ---
    ROULETTE_DIRECT_HIGH_ODD("directHighOdd", "直注高赔率", "directHighOdd"),
    ROULETTE_DIRECT("direct", "直注", "direct"),
    ROULETTE_SEPARATE("separate", "分注", "separate"),
    ROULETTE_STREET("street", "街注", "street"),
    ROULETTE_ANGLE("angle", "角注", "angle"),
    ROULETTE_LINE("line", "线注", "line"),
    ROULETTE_THREE("three", "三数注", "three"),

    ROULETTE_FOUR("four", "四个号码", "four"),
    ROULETTE_FIRST_ROW("firstRow", "行注一", "firstRow"),
    ROULETTE_SND_ROW("sndRow", "行注二", "sndRow"),
    ROULETTE_THR_ROW("thrRow", "行注三", "thrRow"),

    ROULETTE_FIRST_COL("firstCol", "打注一", "firstCol"),
    ROULETTE_SND_COL("sndCol", "打注二", "sndCol"),
    ROULETTE_THR_COL("thrCol", "打注三", "thrCol"),

    ROULETTE_RED("red", "红", "red"),
    ROULETTE_BLACK("black", "黑", "black"),
    ROULETTE_ODD("odd", "单", "odd"),
    ROULETTE_EVEN("even", "双", "even"),
    ROULETTE_LOW("low", "小", "low"),
    ROULETTE_HIGH("high", "大", "high"),

    //骰宝
    SB_BIG("big", "大", "big"),
    SB_SMALL("small", "小", "small"),
    SB_ODD("odd", "单", "odd"),
    SB_EVEN("even", "双", "even"),
    SB_ALL_DICES("allDices", "全围", "allDices"),
    SB_THREE_FORCES("threeForces", "三军", "threeForces"),
    SB_NINE_WAY_GARDS("nineWayGards", "短牌", "nineWayGards"),
    SB_PAIRS("pairs", "长牌", "pairs"),
    SB_SURROUND_DICES("surroundDices", "围骰", "surroundDices"),
    SB_POINTS("points", "点数", "points"),

    //牛牛
    COW_PLAYER1_DOUBLE("player1Double", "闲一翻倍", "player1Double"),
    COW_PLAYER2_DOUBLE("player2Double", "闲二翻倍", "player2Double"),
    COW_PLAYER3_DOUBLE("player3Double", "闲三翻倍", "player3Double"),

    COW_PLAYER1_EQUAL("player1Equal", "闲一平倍", "player1Equal"),
    COW_PLAYER2_EQUAL("player2Equal", "闲二平倍", "player2Equal"),
    COW_PLAYER3_EQUAL("player3Equal", "闲三平倍", "player3Equal"),

    COW_PLAYER1_MANY("player1Many", "闲一多倍", "player1Many"),
    COW_PLAYER2_MANY("player2Many", "闲二多倍", "player2Many"),
    COW_PLAYER3_MANY("player3Many", "闲三多倍", "player3Many"),

    COW_BANKER1_DOUBLE("banker1Double", "庄一翻倍", "banker1Double"),
    COW_BANKER2_DOUBLE("banker2Double", "庄二翻倍", "banker2Double"),
    COW_BANKER3_DOUBLE("banker3Double", "庄三翻倍", "banker3Double"),

    COW_BANKER1_EQUAL("banker1Equal", "庄一平倍", "banker1Equal"),
    COW_BANKER2_EQUAL("banker2Equal", "庄二平倍", "banker2Equal"),
    COW_BANKER3_EQUAL("banker3Equal", "庄三平倍", "banker3Equal"),

    COW_BANKER1_MANY("banker1Many", "庄一多倍", "banker1Many"),
    COW_BANKER2_MANY("banker2Many", "庄二多倍", "banker2Many"),
    COW_BANKER3_MANY("banker3Many", "庄三多倍", "banker3Many"),

    //炸金花
    ZJH_RED("red", "红", "red"),
    ZJH_BLACK("black", "黑", "black"),
    ZJH_LUCK("luck", "幸运一击", "luck"),
    ZJH_FLUSH("flush", "同花", "flush"),
    ZJH_STRAIGHT("straight", "顺子", "straight"),
    ZJH_THREE_KIND("threeKind", "豹子", "threeKind"),
    ZJH_STRAIGHT_FLUSH("straightFlush", "同花顺", "straightFlush"),

    //色碟
    SedieZERO("zero", "4白", "zero"),
    SedieONE("one", "3白1红", "one"),
    SedieTHREE("three", "3红1白", "three"),
    SedieFOUR("four", "4红", "four"),
    SedieODD("odd", "单", "odd"),
    SedieEVEN("even", "双", "even"),
    SedieBIG("big", "大", "big"),
    SedieSMALL("small", "小", "small"),
    SedieBIGH("bigH", "高赔大", "bigH"),
    SedieSMALLH("smallH", "高赔小", "smallH"),
    SedieTWO("two", "2红2白", "two"),
    SedieZERO_FOUR("zeroFour", "4红或4白", "zeroFour"),

    //番摊
    FT_ODD("odd", "单", "odd"),
    FT_EVEN("even", "双", "even"),
    FT_FAN("fan", "番", "fan"),
    FT_JIAO("jiao", "角", "jiao"),
    FT_SANMEN("sanmen", "三门", "sanmen"),
    FT_NIAN("nian", "念", "nian"),
    FT_TONG("tong", "通", "tong"),


    //三公
    SG_BANKER1("banker1", "庄一;", "banker1"),
    SG_BANKER2("banker2", "庄二;", "banker2"),
    SG_BANKER3("banker3", "庄三;", "banker3"),
    SG_PLAYER1("player1", "闲一;", "player1"),
    SG_PLAYER2("player2", "闲二;", "player2"),
    SG_PLAYER3("player3", "闲三;", "player3"),
    SG_TIE1("tie1", "和一;", "tie1"),
    SG_TIE2("tie2", "和二;", "tie2"),
    SG_TIE3("tie3", "和三;", "tie3"),
    SG_TP1("tp1", "闲一三公;", "tp1"),
    SG_TP2("tp2", "闲二三公;", "tp2"),
    SG_TP3("tp3", "闲三三公;", "tp3"),
    SG_PAIR1("pair1", "对牌以上1;", "pair1"),
    SG_PAIR2("pair2", "对牌以上2;", "pair2"),
    SG_PAIR3("pair3", "对牌以上3;", "pair3"),
    SG_BANKER_PAIR("bankerPair", "庄对牌以上;", "bankerPair"),
    SG_BANKER_TP("bankerTp", "庄三公;", "bankerTp"),

    //安达巴哈
    ANDA_BET_ANDAR("andar", "安达", "andar"),
    ANDA_BET_BAHAR("bahar", "巴哈", "bahar"),
    ANDA_BET_ANDAR_NO("andarNo", "安达首张", "andarNo"),
    ANDA_BET_BAHAR_NO("baharNo", "巴哈首张", "baharNo"),
    ANDA_BET_POINTS_1("points1", "1-5张牌出", "points1"),
    ANDA_BET_POINTS_2("points2", "6-10张牌出", "points2"),
    ANDA_BET_POINTS_3("points3", "11-15张牌出", "points3"),
    ANDA_BET_POINTS_4("points4", "16-25张牌出", "points4"),
    ANDA_BET_POINTS_5("points5", "26-30张牌出", "points5"),
    ANDA_BET_POINTS_6("points6", "31-35张牌出", "points6"),
    ANDA_BET_POINTS_7("points7", "36-40张牌出", "points7"),
    ANDA_BET_POINTS_8("points8", "41+张牌出", "points8"),

    //21点


    SEAT_SEAT("seat", "座位值", "seat"),
    SEAT_BASE("base", "入座", "base"),
    SEAT_BASE_SIDE("base_side", "旁注", "base_side"),
    SEAT_BASE1("base1", "第一手牌", "base1"),
    SEAT_BASE2("base2", "第二手牌", "base2"),
    SEAT_PERFECT_PAIR("perfectPair", "对子", "perfectPair"),
    SEAT_THREE_CARD("threeCard", "21+3", "threeCard"),
    SEAT_BASE_BX("baseBx", "保险", "baseBx"),
    SEAT_REDOUBLE1("redouble1", "第一手牌加倍", "redouble1"),
    SEAT_REDOUBLE2("redouble2", "第二手牌加倍", "redouble2");




    private final String code;
    private final String desc;
    private final String shCode;

    DG2PlayTypeEnum(String code, String desc, String shCode) {
        this.code = code;
        this.desc = desc;
        this.shCode = shCode;
    }

    // 根据属性名查找枚举
    public static Optional<DG2PlayTypeEnum> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }

    public static DG2PlayTypeEnum getEnum(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
    }
}
