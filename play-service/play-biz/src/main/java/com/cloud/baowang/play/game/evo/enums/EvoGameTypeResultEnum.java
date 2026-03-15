package com.cloud.baowang.play.game.evo.enums;

import lombok.Getter;

/**
 * 参考
 */
@Getter
public enum EvoGameTypeResultEnum {

    AMERICAN_RED_DOOR_ROULETTE("americanreddoorroulette", "美国红门轮盘"),
    BACCARAT("baccarat", "百家乐"),
    BLACKJACK("blackjack", "二十一点"),
    ROULETTE("roulette", "轮盘"),
    AMERICAN_ROULETTE("americanroulette", "美式轮盘"),
    CASINO_HOLDEM("holdem", "赌场德州扑克"),
    THREE_CARD_POKER("tcp", "三张扑克"),
    CARIBBEAN_STUD_POKER("csp", "加勒比梭哈"),
    TRIPLE_CARD_POKER("trp", "三倍扑克"),
    ULTIMATE_TEXAS_HOLDEM("uth", "终极德州扑克"),
    EXTREME_TEXAS_HOLDEM("eth", "极限德州扑克"),
    TEXAS_HOLDEM_BONUS("thb", "德州扑克奖励"),
    DOUBLE_HAND_CASINO_HOLDEM_POKER("dhp", "双手德州扑克"),
    CLASSIC_FREE_BET("classicfreebet", "经典免下注"),
    CLASSIC_ALWAYS_6_BLACKJACK("classicalways6bj", "经典常六黑杰克"),
    CLASSIC_BET_STACKER_BLACKJACK("classicbetstackerbj", "经典叠注黑杰克"),
    MONEY_WHEEL("moneywheel", "幸运转盘"),
    DRAGON_TIGER("dragontiger", "龙虎斗"),
    TOP_CARD("topcard", "顶牌"),
    SCALABLE_BLACKJACK("scalableblackjack", "可扩展黑杰克"),
    SCALABLE_BET_STACKER_BLACKJACK("scalablebetstackerbj", "可扩展叠注黑杰克"),
    POWER_SCALABLE_BLACKJACK("powerscalableblackjack", "动力可扩展黑杰克"),
    FREEBET_BLACKJACK("freebet", "免下注黑杰克"),
    LIGHTNING_BLACKJACK("lightningscalablebj", "闪电二十一点"),
    MONOPOLY("monopoly", "大富翁"),
    MONOPOLY_BIG_BALLER("monopolybigballer", "大富翁宾果球"),
    DEAL_OR_NO_DEAL("dealnodeal", "一掷千金"),
    LIGHTNING_DICE("lightningdice", "闪电骰子"),
    TOP_DICE("topdice", "顶级骰子"),
    SIC_BO("sicbo", "骰宝"),
    LIGHTNING_SIC_BO("sicbo", "闪电骰宝"),
    SIDEBET_CITY("sidebetcity", "边注之城"),
    MEGA_BALL("megaball", "超级球"),
    CRAPS("craps", "掷骰子"),
    TEEN_PATTI("teenpatti", "印度三张牌"),
    THREE_CARD("threecard", "三张牌"),
    CRAZY_TIME("crazytime", "疯狂时间"),
    INSTANT_ROULETTE("instantroulette", "即时轮盘"),
    GONZO_TREASURE_MAP("gonzotreasuremap", "冈萨罗寻宝图"),
    CASH_OR_CRASH("cashorcrash", "现金或坠落"),
    FAN_TAN("fantan", "番摊"),
    BAC_BO("bacbo", "百家乐骰宝 / 第一人称"),
    SUPER_ANDAR_BAHAR("andarbahar", "超级安达巴哈"),
    DEAD_OR_ALIVE_SALOON("deadoralivesaloon", "生死酒馆"),
    CRAZY_COIN_FLIP("crazycoinflip", "疯狂抛硬币"),
    EXTRA_CHILLI_EPIC_SPINS("extrachilliepicspins", "额外辣椒旋转"),
    FUNKY_TIME("funkytime", "炫酷时间"),
    GOLD_VAULT_ROULETTE("goldvault", "黄金保险库轮盘"),
    POWERBALL("powerball", "强力球"),
    VIDEO_POKER("videopoker", "视频扑克"),
    LIGHTNING_LOTTO("lightninglotto", "闪电乐透"),
    CRAZY_PACHINKO("crazypachinko", "疯狂柏青哥"),
    STOCK_MARKET("stockmarket", "股票市场"),
    SLOT_GAMES("10001nights", "老虎机游戏"),
    LIGHTNING_DRAGON_TIGER("lightning", "闪电龙虎斗"),
    FIRST_PERSON_HI_LO("rng-hilo", "第一人称大小"),
    INFINITE_FUN_FUN_21_BLACKJACK("funfun21scalablebj", "无限趣味21点"),
    REDDOOR_ROULETTE_SUBTYPE("reddoorroulette", "红门轮盘子类型"),
    LIGHTNING_STORM("lightningstorm", "闪电风暴"),
    BALLOON_RACE("balloonrace", "气球竞赛"),
    ROULETTE_B("roulette", "轮盘B"),
    LIGHTNING_BALL("lightningball", "闪电球"),
    LIVE_SLOT_DEAL_OR_NO_DEAL("liveslotdealnodeal", "现场老虎机一掷千金"),
    CRAZY_BALLS("crazyballs", "疯狂小球"),
    RACE_TRACK_RNG("racetrack", "赛道 & RNG赛道"),
    EASY_BLACKJACK("easybj", "简单二十一点"),
    MARBLE_RACE("marblerace", "弹珠赛跑"),
    WAR("war", "战争"),
    SUPER_COLOR_GAME("supercolorgame", "超级颜色游戏"),
    FIREBALL_ROULETTE("fireballroulette", "火球轮盘"),
    ICE_FISHING("icefishing", "冰钓"),
    RED_BARON("redbaron", "红男爵");

    private final String gameName;  // 英文名（小写）
    private final String cnName;    // 中文名

    EvoGameTypeResultEnum(String gameName, String cnName) {
        this.gameName = gameName;
        this.cnName = cnName;
    }
}
