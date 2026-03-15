package com.cloud.baowang.play.game.sexy.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SexyPlayTypeEnum {
    BANKER("Banker", "庄","BJL_BANKER_COMM"),
    PLAYER("Player", "闲","BJL_PLAYER"),
    TIE("Tie", "和","BJL_TIE"),
    B_PAIR("BankerPair", "庄对","BJL_BANKER_PAIR"),
    P_PAIR("PlayerPair", "闲对","BJL_PLAYER_PAIR"),
    B_BONUS("BankerBonus", "庄龙宝","BJL_BANKER_DRAGON"),
    P_BONUS("PlayerBonus", "闲龙宝","BJL_BANKER_DRAGON"),
    B_BX_PRE("Banker Insurance (P First 2 cards)", "庄保险 (闲补牌前)","BANKER_INSURANCE"), //todo
    B_BX("Banker Insurance (P 3 cards)", "庄保险 (闲补牌后)","PLAYER_INSURANCE"),//todo

    P_BX_PRE("Player Insurance (P First 2 cards)", "闲保险 (闲补牌前)","BANKER_INSURANCE"),//todo
    P_BX("Player Insurance (P 3 cards)", "闲保险 (闲补牌后)","PLAYER_INSURANCE"),//todo
    BIG("Big", "大","CD_BIG"),
    SMALL("Small", "小","CD_SMALL"),
    NO_COMMON_BANKER("NoCommBanker", "免佣庄","BJL_BANKER_NO_COMM"),
    SUPER6("SuperSix", "幸运六","BJL_SUPER_SIX"),
    ANY_PAIR("Turtle", "任意对子","BJL_EITHER_PAIR"),
    PERFECT_PAIR("Phoenix", "完美对子","BJL_PERFECT_PAIR"),

    /** 龙虎 */
    TIGER("tiger", "虎","BJL_TIGER"),
    DT_TIE("dtTie", "龙虎和","BJL_DRAGON_TIGER_TIE"),
    DRAGON("dragon", "龙","BJL_DRAGON"),


    /** 骰宝 */
    SB_BIG("Big","大","SB_BIG"),
    SB_SMALL("Small","小","SB_SMALL"),
    SB_ODD("Odd","单","SB_ODD"),
    SB_EVEN("Even","双","SB_EVEN"),
    SB_4("Sum 4","和值4","SB_4"),
    SB_5("Sum 5","和值5","SB_5"),
    SB_6("Sum 6","和值6","SB_6"),

    SB_7("Sum 7","和值7","SB_7"),
    SB_8("Sum 8","和值8","SB_8"),
    SB_9("Sum 9","和值9","SB_9"),
    SB_10("Sum 10","和值10","SB_10"),
    SB_11("Sum 11","和值11","SB_11"),
    SB_12("Sum 12","和值12","SB_12"),
    SB_13("Sum 13","和值13","SB_13"),
    SB_14("Sum 14","和值14","SB_14"),
    SB_15("Sum 15","和值15","SB_15"),
    SB_16("Sum 16","和值16","SB_16"),
    SB_17("Sum 17","和值17","SB_17"),
    SB_TUO_1("Single 1","单点1","SB_TUO_1"),
    SB_TUO_2("Single 2","单点2","SB_TUO_2"),
    SB_TUO_3("Single 3","单点3","SB_TUO_3"),
    SB_TUO_4("Single 4","单点4","SB_TUO_4"),
    SB_TUO_5("Single 5","单点5","SB_TUO_5"),
    SB_TUO_6("Single 6","单点6","SB_TUO_6"),

    SB_1_1("Double 1","对子1","SB_1_1"),
    SB_2_2("Double 2","对子2","SB_2_2"),
    SB_3_3("Double 3","对子3","SB_3_3"),
    SB_4_4("Double 4","对子4","SB_4_4"),
    SB_5_5("Double 5","对子5","SB_5_5"),
    SB_6_6("Double 6","对子6","SB_6_6"),

    SB_1_2("Combine 12","牌九式12","SB_1_2"),
    SB_1_3("Combine 13","牌九式13","SB_1_3"),
    SB_1_4("Combine 14","牌九式14","SB_1_4"),
    SB_1_5("Combine 15","牌九式15","SB_1_5"),
    SB_1_6("Combine 16","牌九式16","SB_1_6"),
    SB_2_3("Combine 23","牌九式23","SB_2_3"),
    SB_2_4("Combine 24","牌九式24","SB_2_4"),
    SB_2_5("Combine 25","牌九式25","SB_2_5"),
    SB_2_6("Combine 26","牌九式26","SB_2_6"),
    SB_3_4("Combine 34","牌九式34","SB_3_4"),
    SB_3_5("Combine 35","牌九式35","SB_3_5"),
    SB_3_6("Combine 36","牌九式36","SB_3_6"),
    SB_4_5("Combine 45","牌九式45","SB_4_5"),
    SB_4_6("Combine 46","牌九式46","SB_4_6"),
    SB_5_6("Combine 56","牌九式56","SB_5_6"),

    SB_1_1_1("Triple 1","围骰1","SB_1_1_1"),
    SB_2_2_2("Triple 1","围骰2","SB_2_2_2"),
    SB_3_3_3("Triple 1","围骰3","SB_3_3_3"),
    SB_4_4_4("Triple 1","围骰4","SB_4_4_4"),
    SB_5_5_5("Triple 1","围骰5","SB_5_5_5"),
    SB_6_6_6("Triple 1","围骰6","SB_6_6_6"),
    SB_ALL_WEI("Triple 1","全围","SB_ALL_WEI"),

    // --- 轮盘 ---
    RT_SMALL("Small", "小", "SMALL"),
    RT_BIG("Big", "大", "BIG"),
    RT_SHUANG("Even", "双", "SHUANG"),
    RT_DAN("Odd", "单", "DAN"),
    RT_RED("Red", "红", "RED"),
    RT_BLACK("Black", "黑", "BLACK"),

    LIST_BET_1("Column 1st", "第一列", "LIST_BET_1"),
    LIST_BET_2("Column 2nd", "第二列", "LIST_BET_2"),
    LIST_BET_3("Column 3rd", "第三列", "LIST_BET_3"),

    DOZEN_BET_1("Dozen 1st", "第一打", "DOZEN_BET_1"),
    DOZEN_BET_2("Dozen 2nd", "第二打", "DOZEN_BET_2"),
    DOZEN_BET_3("Dozen 3rd", "第三打", "DOZEN_BET_3"),

    FOUR_BET("Four Numbers 0,1,2,3", "四个号码", "FOUR_BET"),
    THREE_BET_0_1_2("Triangle 0,1,2", "三数_0_1_2", "THREE_BET_0_1_2"),
    THREE_BET_0_2_3("Triangle 0,2,3", "三数_0_2_3", "THREE_BET_0_2_3"),

    DIRECT_BET_0("Direct 0", "直注0", "DIRECT_BET_0"),
    DIRECT_BET_1("Direct 1", "直注1", "DIRECT_BET_1"),
    DIRECT_BET_2("Direct 2", "直注2", "DIRECT_BET_2"),
    DIRECT_BET_3("Direct 3", "直注3", "DIRECT_BET_3"),
    DIRECT_BET_4("Direct 4", "直注4", "DIRECT_BET_4"),
    DIRECT_BET_5("Direct 5", "直注5", "DIRECT_BET_5"),
    DIRECT_BET_6("Direct 6", "直注6", "DIRECT_BET_6"),
    DIRECT_BET_7("Direct 7", "直注7", "DIRECT_BET_7"),
    DIRECT_BET_8("Direct 8", "直注8", "DIRECT_BET_8"),
    DIRECT_BET_9("Direct 9", "直注9", "DIRECT_BET_9"),
    DIRECT_BET_10("Direct 10", "直注10", "DIRECT_BET_10"),
    DIRECT_BET_11("Direct 11", "直注11", "DIRECT_BET_11"),
    DIRECT_BET_12("Direct 12", "直注12", "DIRECT_BET_12"),
    DIRECT_BET_13("Direct 13", "直注13", "DIRECT_BET_13"),
    DIRECT_BET_14("Direct 14", "直注14", "DIRECT_BET_14"),
    DIRECT_BET_15("Direct 15", "直注15", "DIRECT_BET_15"),
    DIRECT_BET_16("Direct 16", "直注16", "DIRECT_BET_16"),
    DIRECT_BET_17("Direct 17", "直注17", "DIRECT_BET_17"),
    DIRECT_BET_18("Direct 18", "直注18", "DIRECT_BET_18"),
    DIRECT_BET_19("Direct 19", "直注19", "DIRECT_BET_19"),
    DIRECT_BET_20("Direct 20", "直注20", "DIRECT_BET_20"),
    DIRECT_BET_21("Direct 21", "直注21", "DIRECT_BET_21"),
    DIRECT_BET_22("Direct 22", "直注22", "DIRECT_BET_22"),
    DIRECT_BET_23("Direct 23", "直注23", "DIRECT_BET_23"),
    DIRECT_BET_24("Direct 24", "直注24", "DIRECT_BET_24"),
    DIRECT_BET_25("Direct 25", "直注25", "DIRECT_BET_25"),
    DIRECT_BET_26("Direct 26", "直注26", "DIRECT_BET_26"),
    DIRECT_BET_27("Direct 27", "直注27", "DIRECT_BET_27"),
    DIRECT_BET_28("Direct 28", "直注28", "DIRECT_BET_28"),
    DIRECT_BET_29("Direct 29", "直注29", "DIRECT_BET_29"),
    DIRECT_BET_30("Direct 30", "直注30", "DIRECT_BET_30"),
    DIRECT_BET_31("Direct 31", "直注31", "DIRECT_BET_31"),
    DIRECT_BET_32("Direct 32", "直注32", "DIRECT_BET_32"),
    DIRECT_BET_33("Direct 33", "直注33", "DIRECT_BET_33"),
    DIRECT_BET_34("Direct 34", "直注34", "DIRECT_BET_34"),
    DIRECT_BET_35("Direct 35", "直注35", "DIRECT_BET_35"),
    DIRECT_BET_36("Direct 36", "直注36", "DIRECT_BET_36"),

    SEPARATE_BET_0_1("Separate 0,1", "分注0_1", "SEPARATE_BET_0_1"),
    SEPARATE_BET_0_2("Separate 0,2", "分注0_2", "SEPARATE_BET_0_2"),
    SEPARATE_BET_0_3("Separate 0,3", "分注0_3", "SEPARATE_BET_0_3"),

    SEPARATE_BET_1_2("Separate 1,2", "分注1_2", "SEPARATE_BET_1_2"),
    SEPARATE_BET_1_4("Separate 1,4", "分注1_4", "SEPARATE_BET_1_4"),

    SEPARATE_BET_2_3("Separate 2,3", "分注2_3", "SEPARATE_BET_2_3"),
    SEPARATE_BET_2_5("Separate 2,5", "分注2_5", "SEPARATE_BET_2_5"),

    SEPARATE_BET_3_6("Separate 3,6", "分注3_6", "SEPARATE_BET_3_6"),

    SEPARATE_BET_4_5("Separate 4,5", "分注4_5", "SEPARATE_BET_4_5"),
    SEPARATE_BET_4_7("Separate 4,7", "分注4_7", "SEPARATE_BET_4_7"),

    SEPARATE_BET_5_6("Separate 5,6", "分注5_6", "SEPARATE_BET_5_6"),
    SEPARATE_BET_5_8("Separate 5,8", "分注5_8", "SEPARATE_BET_5_8"),

    SEPARATE_BET_6_9("Separate 6,9", "分注6_9", "SEPARATE_BET_6_9"),

    SEPARATE_BET_7_8("Separate 7,8", "分注7_8", "SEPARATE_BET_7_8"),
    SEPARATE_BET_7_10("Separate 7,10", "分注7_10", "SEPARATE_BET_7_10"),

    SEPARATE_BET_8_9("Separate 8,9", "分注8_9", "SEPARATE_BET_8_9"),
    SEPARATE_BET_8_11("Separate 8,11", "分注8_11", "SEPARATE_BET_8_11"),

    SEPARATE_BET_9_12("Separate 9,12", "分注9_12", "SEPARATE_BET_9_12"),

    SEPARATE_BET_10_11("Separate 10,11", "分注10_11", "SEPARATE_BET_10_11"),
    SEPARATE_BET_10_13("Separate 10,13", "分注10_13", "SEPARATE_BET_10_13"),

    SEPARATE_BET_11_12("Separate 11,12", "分注11_12", "SEPARATE_BET_11_12"),
    SEPARATE_BET_11_14("Separate 11,14", "分注11_14", "SEPARATE_BET_11_14"),

    SEPARATE_BET_12_15("Separate 12,15", "分注12_15", "SEPARATE_BET_12_15"),

    SEPARATE_BET_13_14("Separate 13,14", "分注13_14", "SEPARATE_BET_13_14"),
    SEPARATE_BET_13_16("Separate 13,16", "分注13_16", "SEPARATE_BET_13_16"),

    SEPARATE_BET_14_15("Separate 14,15", "分注14_15", "SEPARATE_BET_14_15"),
    SEPARATE_BET_14_17("Separate 14,17", "分注14_17", "SEPARATE_BET_14_17"),

    SEPARATE_BET_15_18("Separate 15,18", "分注15_18", "SEPARATE_BET_15_18"),

    SEPARATE_BET_16_17("Separate 16,17", "分注16_17", "SEPARATE_BET_16_17"),
    SEPARATE_BET_16_19("Separate 16,19", "分注16_19", "SEPARATE_BET_16_19"),

    SEPARATE_BET_17_18("Separate 17,18", "分注17_18", "SEPARATE_BET_17_18"),
    SEPARATE_BET_17_20("Separate 17,20", "分注17_20", "SEPARATE_BET_17_20"),

    SEPARATE_BET_18_21("Separate 18,21", "分注18_21", "SEPARATE_BET_18_21"),

    SEPARATE_BET_19_20("Separate 19,20", "分注19_20", "SEPARATE_BET_19_20"),
    SEPARATE_BET_19_22("Separate 19,22", "分注19_22", "SEPARATE_BET_19_22"),

    SEPARATE_BET_20_21("Separate 20,21", "分注20_21", "SEPARATE_BET_20_21"),
    SEPARATE_BET_20_23("Separate 20,23", "分注20_23", "SEPARATE_BET_20_23"),

    SEPARATE_BET_21_24("Separate 21,24", "分注21_24", "SEPARATE_BET_21_24"),

    SEPARATE_BET_22_23("Separate 22,23", "分注22_23", "SEPARATE_BET_22_23"),
    SEPARATE_BET_22_25("Separate 22,25", "分注22_25", "SEPARATE_BET_22_25"),

    SEPARATE_BET_23_24("Separate 23,24", "分注23_24", "SEPARATE_BET_23_24"),
    SEPARATE_BET_23_26("Separate 23,26", "分注23_26", "SEPARATE_BET_23_26"),

    SEPARATE_BET_24_27("Separate 24,27", "分注24_27", "SEPARATE_BET_24_27"),

    SEPARATE_BET_25_26("Separate 25,26", "分注25_26", "SEPARATE_BET_25_26"),
    SEPARATE_BET_25_28("Separate 25,28", "分注25_28", "SEPARATE_BET_25_28"),

    SEPARATE_BET_26_27("Separate 26,27", "分注26_27", "SEPARATE_BET_26_27"),
    SEPARATE_BET_26_29("Separate 26,29", "分注26_29", "SEPARATE_BET_26_29"),

    SEPARATE_BET_27_30("Separate 27,30", "分注27_30", "SEPARATE_BET_27_30"),

    SEPARATE_BET_28_29("Separate 28,29", "分注28_29", "SEPARATE_BET_28_29"),
    SEPARATE_BET_28_31("Separate 28,31", "分注28_31", "SEPARATE_BET_28_31"),

    SEPARATE_BET_29_30("Separate 29,30", "分注29_30", "SEPARATE_BET_29_30"),
    SEPARATE_BET_29_32("Separate 29,32", "分注29_32", "SEPARATE_BET_29_32"),

    SEPARATE_BET_30_33("Separate 30,33", "分注30_33", "SEPARATE_BET_30_33"),

    SEPARATE_BET_31_32("Separate 31,32", "分注31_32", "SEPARATE_BET_31_32"),
    SEPARATE_BET_31_34("Separate 31,34", "分注31_34", "SEPARATE_BET_31_34"),

    SEPARATE_BET_32_33("Separate 32,33", "分注32_33", "SEPARATE_BET_32_33"),
    SEPARATE_BET_32_35("Separate 32,35", "分注32_35", "SEPARATE_BET_32_35"),

    SEPARATE_BET_33_36("Separate 33,36", "分注33_36", "SEPARATE_BET_33_36"),

    SEPARATE_BET_34_35("Separate 34,35", "分注34_35", "SEPARATE_BET_34_35"),

    SEPARATE_BET_35_36("Separate 35,36", "分注35_36", "SEPARATE_BET_35_36"),

    /** 街注 */
    STREET_BET_1_2_3("Street 1,2,3", "街注1_2_3", "STREET_BET_1_2_3"),
    STREET_BET_4_5_6("Street 4,5,6", "街注4_5_6", "STREET_BET_4_5_6"),
    STREET_BET_7_8_9("Street 7,8,9", "街注7_8_9", "STREET_BET_7_8_9"),
    STREET_BET_10_11_12("Street 10,11,12", "街注10_11_12", "STREET_BET_10_11_12"),
    STREET_BET_13_14_15("Street 13,14,15", "街注13_14_15", "STREET_BET_13_14_15"),
    STREET_BET_16_17_18("Street 16,17,18", "街注16_17_18", "STREET_BET_16_17_18"),
    STREET_BET_19_20_21("Street 19,20,21", "街注19_20_21", "STREET_BET_19_20_21"),
    STREET_BET_22_23_24("Street 22,23,24", "街注22_23_24", "STREET_BET_22_23_24"),
    STREET_BET_25_26_27("Street 25,26,27", "街注25_26_27", "STREET_BET_25_26_27"),
    STREET_BET_28_29_30("Street 28,29,30", "街注28_29_30", "STREET_BET_28_29_30"),
    STREET_BET_31_32_33("Street 31,32,33", "街注31_32_33", "STREET_BET_31_32_33"),
    STREET_BET_34_35_36("Street 34,35,36", "街注34_35_36", "STREET_BET_34_35_36"),

    /** 角注 */
    HORN_BET_1_2_4_5("Corner 1,2,4,5", "角注1_2_4_5", "HORN_BET_1_2_4_5"),
    HORN_BET_2_3_5_6("Corner 2,3,5,6", "角注2_3_5_6", "HORN_BET_2_3_5_6"),
    HORN_BET_4_5_7_8("Corner 4,5,7,8", "角注4_5_7_8", "HORN_BET_4_5_7_8"),
    HORN_BET_5_6_8_9("Corner 5,6,8,9", "角注5_6_8_9", "HORN_BET_5_6_8_9"),
    HORN_BET_7_8_10_11("Corner 7,8,10,11", "角注7_8_10_11", "HORN_BET_7_8_10_11"),
    HORN_BET_8_9_11_12("Corner 8,9,11,12", "角注8_9_11_12", "HORN_BET_8_9_11_12"),
    HORN_BET_10_11_13_14("Corner 10,11,13,14", "角注10_11_13_14", "HORN_BET_10_11_13_14"),
    HORN_BET_11_12_14_15("Corner 11,12,14,15", "角注11_12_14_15", "HORN_BET_11_12_14_15"),
    HORN_BET_13_14_16_17("Corner 13,14,16,17", "角注13_14_16_17", "HORN_BET_13_14_16_17"),
    HORN_BET_14_15_17_18("Corner 14,15,17,18", "角注14_15_17_18", "HORN_BET_14_15_17_18"),
    HORN_BET_16_17_19_20("Corner 16,17,19,20", "角注16_17_19_20", "HORN_BET_16_17_19_20"),
    HORN_BET_17_18_20_21("Corner 17,18,20,21", "角注17_18_20_21", "HORN_BET_17_18_20_21"),
    HORN_BET_19_20_22_23("Corner 19,20,22,23", "角注19_20_22_23", "HORN_BET_19_20_22_23"),
    HORN_BET_20_21_23_24("Corner 20,21,23,24", "角注20_21_23_24", "HORN_BET_20_21_23_24"),
    HORN_BET_22_23_25_26("Corner 22,23,25,26", "角注22_23_25_26", "HORN_BET_22_23_25_26"),
    HORN_BET_23_24_26_27("Corner 23,24,26,27", "角注23_24_26_27", "HORN_BET_23_24_26_27"),
    HORN_BET_25_26_28_29("Corner 25,26,28,29", "角注25_26_28_29", "HORN_BET_25_26_28_29"),
    HORN_BET_26_27_29_30("Corner 26,27,29,30", "角注26_27_29_30", "HORN_BET_26_27_29_30"),
    HORN_BET_28_29_31_32("Corner 28,29,31,32", "角注28_29_31_32", "HORN_BET_28_29_31_32"),
    HORN_BET_29_30_32_33("Corner 29,30,32,33", "角注29_30_32_33", "HORN_BET_29_30_32_33"),
    HORN_BET_31_32_34_35("Corner 31,32,34,35", "角注31_32_34_35", "HORN_BET_31_32_34_35"),
    HORN_BET_32_33_35_36("Corner 32,33,35,36", "角注32_33_35_36", "HORN_BET_32_33_35_36"),

    /** 线注 */
    WIRE_BET_1_2_3_4_5_6("Line 1,2,3,4,5,6", "线注1_2_3_4_5_6", "WIRE_BET_1_2_3_4_5_6"),
    WIRE_BET_4_5_6_7_8_9("Line 4,5,6,7,8,9", "线注4_5_6_7_8_9", "WIRE_BET_4_5_6_7_8_9"),
    WIRE_BET_7_8_9_10_11_12("Line 7,8,9,10,11,12", "线注7_8_9_10_11_12", "WIRE_BET_7_8_9_10_11_12"),
    WIRE_BET_10_11_12_13_14_15("Line 10,11,12,13,14,15", "线注10_11_12_13_14_15", "WIRE_BET_10_11_12_13_14_15"),
    WIRE_BET_13_14_15_16_17_18("Line 13,14,15,16,17,18", "线注13_14_15_16_17_18", "WIRE_BET_13_14_15_16_17_18"),
    WIRE_BET_16_17_18_19_20_21("Line 16,17,18,19,20,21", "线注16_17_18_19_20_21", "WIRE_BET_16_17_18_19_20_21"),
    WIRE_BET_19_20_21_22_23_24("Line 19,20,21,22,23,24", "线注19_20_21_22_23_24", "WIRE_BET_19_20_21_22_23_24"),
    WIRE_BET_22_23_24_25_26_27("Line 22,23,24,25,26,27", "线注22_23_24_25_26_27", "WIRE_BET_22_23_24_25_26_27"),
    WIRE_BET_25_26_27_28_29_30("Line 25,26,27,28,29,30", "线注25_26_27_28_29_30", "WIRE_BET_25_26_27_28_29_30"),
    WIRE_BET_28_29_30_31_32_33("Line 28,29,30,31,32,33", "线注28_29_30_31_32_33", "WIRE_BET_28_29_30_31_32_33"),
    WIRE_BET_31_32_33_34_35_36("Line 31,32,33,34,35,36", "线注31_32_33_34_35_36", "WIRE_BET_31_32_33_34_35_36"),

    /** 鱼虾蟹 */
    FPC_CF("Combine Crab Fish","双骰组合-鱼，蟹","FPCRDouble1_4"),
    FPC_CG("Combine Crab Gourd","双骰组合-蟹，葫芦","FPCRDouble2_4"),
    FPC_CP("Combine Crab Prawn","双骰组合-虾，蟹","FPCRDouble3_4"),
    FPC_CC("Combine Crab Chicken","双骰组合-蟹，公鸡","FPCRDouble4_6"),
    FPC_CT("Combine Crab Tiger","双骰组合-蟹，老虎","FPCRDouble4_5"),
    FPC_FG("Combine Fish Gourd","双骰组合-鱼，葫芦","FPCRDouble1_2"),
    FPC_FP("Combine Fish Prawn","双骰组合-鱼，虾","FPCRDouble1_3"),
    FPC_FC("Combine Fish Chicken","双骰组合-鱼，公鸡","FPCRDouble1_6"),
    FPC_FT("Combine Fish Tiger","双骰组合-鱼，老虎","FPCRDouble1_5"),
    FPC_GP("Combine Gourd Prawn","双骰组合-葫芦，虾","FPCRDouble2_3"),
    FPC_GC("Combine Gourd Chicken","双骰组合-葫芦，公鸡","FPCRDouble2_6"),
    FPC_GT("Combine Gourd Tiger","双骰组合-葫芦，老虎","FPCRDouble2_5"),
    FPC_PC("Combine Prawn Chicken","双骰组合-虾，公鸡","FPCRDouble3_6"),
    FPC_PT("Combine Prawn Tiger","双骰组合-虾，老虎","FPCRDouble3_5"),
    FPC_CK("Combine Chicken Tiger","双骰组合-老虎，公鸡","FPCRDouble5_6"),
    FPC_S_C("Single Crab","图案-蟹","FPCRSingle_Crab"),
    FPC_S_F("Single Fish","图案-鱼","FPCRSingle_Fish"),
    FPC_S_G("Single Gourd","图案-葫芦","FPCRSingle_Gourd"),
    FPC_S_P("Single Prawn","图案-虾","FPCRSingle_Prawn"),
    FPC_S_CK("Single Chicken","图案-公鸡","FPCRSingle_Chicken"),
    FPC_S_T("Single Tiger","图案-老虎","FPCRSingle_Tiger"),

    FPC_Crab("Crab","蟹","Crab"),
    FPC_Fish("Fish","鱼","Fish"),
    FPC_Gourd("Gourd","葫芦","Gourd"),
    FPC_Prawn("Prawn","虾","Prawn"),
    FPC_Chicken("Chicken","公鸡","Chicken"),
    FPC_Tiger("Tiger","老虎","Tiger"),


    //Extra骰宝 - 无i18
    FPC_Hi("Hi", "三骰和 12-18", "Hi"),
    FPC_Lo("Lo", "三骰和 3-10", "Lo"),
    FPC_ElevenHiLo("Eleven HiLo", "三骰和 11", "Eleven HiLo"),
    FPC_Point_1("Point 1", "单骰值 1", "Point 1"),
    FPC_Point_2("Point 2", "单骰值 2", "Point 2"),
    FPC_Point_3("Point 3", "单骰值 3", "Point 3"),
    FPC_Point_4("Point 4", "单骰值 4", "Point 4"),
    FPC_Point_5("Point 5", "单骰值 5", "Point 5"),
    FPC_Point_6("Point 6", "单骰值 6", "Point 6"),
    FPC_Combine_12("Combine 12", "其中两骰为 1, 2", "Combine 12"),
    FPC_Combine_13("Combine 13", "其中两骰为 1, 3", "Combine 13"),
    FPC_Combine_23("Combine 23", "其中两骰为 2, 3", "Combine 23"),
    FPC_Combine_34("Combine 34", "其中两骰为 3, 4", "Combine 34"),
    FPC_Combine_41("Combine 41", "其中两骰为 4, 1", "Combine 41"),
    FPC_Combine_42("Combine 42", "其中两骰为 4, 2", "Combine 42"),
    FPC_Combine_45("Combine 45", "其中两骰为 4, 5", "Combine 45"),
    FPC_Combine_46("Combine 46", "其中两骰为 4, 6", "Combine 46"),
    FPC_Combine_51("Combine 51", "其中两骰为 5, 1", "Combine 51"),
    FPC_Combine_52("Combine 52", "其中两骰为 5, 2", "Combine 52"),
    FPC_Combine_53("Combine 53", "其中两骰为 5, 3", "Combine 53"),
    FPC_Combine_61("Combine 61", "其中两骰为 6, 1", "Combine 61"),
    FPC_Combine_62("Combine 62", "其中两骰为 6, 2", "Combine 62"),
    FPC_Combine_63("Combine 63", "其中两骰为 6, 3", "Combine 63"),
    FPC_Combine_65("Combine 65", "其中两骰为 6, 5", "Combine 65"),
    FPC_Point_123("Point 123", "三骰值 1, 2, 3", "Point 123"),
    FPC_Point_456("Point 456", "三骰值 4, 5, 6", "Point 456"),
    FPC_Point_1_Lo("Point 1 Lo", "三骰和 3-10 任一骰为 1", "Point 1 lo"),
    FPC_Point_2_Lo("Point 2 Lo", "三骰和 3-10 任一骰为 2", "Point 2 lo"),
    FPC_Point_3_Lo("Point 3 Lo", "三骰和 3-10 任一骰为 3", "Point 3 lo"),
    FPC_Point_4_Hi("Point 4 Hi", "三骰和 12-18 任一骰为 4", "Point 4 hi"),
    FPC_Point_5_Hi("Point 5 Hi", "三骰和 12-18 任一骰为 5", "Point 5 hi"),
    FPC_Point_6_Hi("Point 6 Hi", "三骰和 12-18 任一骰为 6", "Point 6 hi"),

    //Extra骰宝
    Extra_SB_AnyTriple("AnyTriple", "全围", "AnyTriple"),
    Extra_SB_Odd("Odd", "单", "Odd"),
    Extra_SB_Even("Even", "双", "Even"),
    Extra_SB_Small("Small", "小", "Small"),
    Extra_SB_Big("Big", "大", "Big"),

    Extra_SB_Sum4("Sum 4", "和值 4", "Sum 4"),
    Extra_SB_Sum5("Sum 5", "和值 5", "Sum 5"),
    Extra_SB_Sum6("Sum 6", "和值 6", "Sum 6"),
    Extra_SB_Sum7("Sum 7", "和值 7", "Sum 7"),
    Extra_SB_Sum8("Sum 8", "和值 8", "Sum 8"),
    Extra_SB_Sum9("Sum 9", "和值 9", "Sum 9"),
    Extra_SB_Sum10("Sum 10", "和值 10", "Sum 10"),
    Extra_SB_Sum11("Sum 11", "和值 11", "Sum 11"),
    Extra_SB_Sum12("Sum 12", "和值 12", "Sum 12"),
    Extra_SB_Sum13("Sum 13", "和值 13", "Sum 13"),
    Extra_SB_Sum14("Sum 14", "和值 14", "Sum 14"),
    Extra_SB_Sum15("Sum 15", "和值 15", "Sum 15"),
    Extra_SB_Sum16("Sum 16", "和值 16", "Sum 16"),
    Extra_SB_Sum17("Sum 17", "和值 17", "Sum 17"),

    Extra_SB_Triple1("Triple 1", "围 1", "Triple 1"),
    Extra_SB_Triple2("Triple 2", "围 2", "Triple 2"),
    Extra_SB_Triple3("Triple 3", "围 3", "Triple 3"),
    Extra_SB_Triple4("Triple 4", "围 4", "Triple 4"),
    Extra_SB_Triple5("Triple 5", "围 5", "Triple 5"),
    Extra_SB_Triple6("Triple 6", "围 6", "Triple 6"),

    Extra_SB_Double1("Double 1", "对子 1", "Double 1"),
    Extra_SB_Double2("Double 2", "对子 2", "Double 2"),
    Extra_SB_Double3("Double 3", "对子 3", "Double 3"),
    Extra_SB_Double4("Double 4", "对子 4", "Double 4"),
    Extra_SB_Double5("Double 5", "对子 5", "Double 5"),
    Extra_SB_Double6("Double 6", "对子 6", "Double 6"),

    Extra_SB_Combine12("Combine 12", "组合 12", "Combine 12"),
    Extra_SB_Combine13("Combine 13", "组合 13", "Combine 13"),
    Extra_SB_Combine14("Combine 14", "组合 14", "Combine 14"),
    Extra_SB_Combine15("Combine 15", "组合 15", "Combine 15"),
    Extra_SB_Combine16("Combine 16", "组合 16", "Combine 16"),
    Extra_SB_Combine23("Combine 23", "组合 23", "Combine 23"),
    Extra_SB_Combine24("Combine 24", "组合 24", "Combine 24"),
    Extra_SB_Combine25("Combine 25", "组合 25", "Combine 25"),
    Extra_SB_Combine26("Combine 26", "组合 26", "Combine 26"),
    Extra_SB_Combine34("Combine 34", "组合 34", "Combine 34"),
    Extra_SB_Combine35("Combine 35", "组合 35", "Combine 35"),
    Extra_SB_Combine36("Combine 36", "组合 36", "Combine 36"),
    Extra_SB_Combine45("Combine 45", "组合 45", "Combine 45"),
    Extra_SB_Combine46("Combine 46", "组合 46", "Combine 46"),
    Extra_SB_Combine56("Combine 56", "组合 56", "Combine 56"),

    Extra_SB_Single1("Single 1", "单点 1", "Single 1"),
    Extra_SB_Single2("Single 2", "单点 2", "Single 2"),
    Extra_SB_Single3("Single 3", "单点 3", "Single 3"),
    Extra_SB_Single4("Single 4", "单点 4", "Single 4"),
    Extra_SB_Single5("Single 5", "单点 5", "Single 5"),
    Extra_SB_Single6("Single 6", "单点 6", "Single 6"),


    //色碟 - 无i18
    SEDIE_ODD("Odd", "单", "Odd"),
    SEDIE_EVEN("Even", "双", "Even"),
    SEDIE_Small("Small", "小", "Small"),
    SEDIE_Big("Big", "大", "Big"),
    SEDIE_4W4R("4 White 4 Red", "3红1白", "4 White 4 Red"),
    SEDIE_4W("4 White", "4白", "4 White"),
    SEDIE_3W1R("3 White 1 Red", "3白1红", "3 White 1 Red"),
    SEDIE_2W2R("2 White 2 Red", "2白2红", "2 White 2 Red"),
    SEDIE_3R1W("3 Red 1 White", "3红1白", "3 Red 1 White"),
    SEDIE_4R("4 Red", "4红", "4 Red"),


    //博丁 - 直接显示英文

    ;



    private final String code;
    private final String desc;
    private final String shCode;

    SexyPlayTypeEnum(String code, String desc, String shCode) {
        this.code = code;
        this.desc = desc;
        this.shCode = shCode;
    }

    // 根据属性名查找枚举
    public static Optional<SexyPlayTypeEnum> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }
}
