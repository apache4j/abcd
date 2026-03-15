package com.cloud.baowang.play.game.sexy.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SexyExtraSBPlayTypeEnum {



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

    SexyExtraSBPlayTypeEnum(String code, String desc, String shCode) {
        this.code = code;
        this.desc = desc;
        this.shCode = shCode;
    }

    // 根据属性名查找枚举
    public static Optional<SexyExtraSBPlayTypeEnum> fromKey(String key) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(key))
                .findFirst();
    }
}
