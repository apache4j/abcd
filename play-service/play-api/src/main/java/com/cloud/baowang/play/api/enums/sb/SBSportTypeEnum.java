package com.cloud.baowang.play.api.enums.sb;

import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum SBSportTypeEnum {
    Sport_1(1, "SPORT_1", "足球", VenueTypeEnum.SPORTS.getCode()),
    Sport_2(2, "Sport_2", "篮球", VenueTypeEnum.SPORTS.getCode()),
    Sport_3(3, "Sport_3", "美式足球", VenueTypeEnum.SPORTS.getCode()),
    Sport_4(4, "Sport_4", "冰上曲棍球", VenueTypeEnum.SPORTS.getCode()),
    Sport_5(5, "Sport_5", "网球", VenueTypeEnum.SPORTS.getCode()),
    Sport_6(6, "Sport_6", "排球", VenueTypeEnum.SPORTS.getCode()),
    Sport_7(7, "Sport_7", "斯诺克/台球", VenueTypeEnum.SPORTS.getCode()),
    Sport_8(8, "Sport_8", "棒球", VenueTypeEnum.SPORTS.getCode()),
    Sport_9(9, "Sport_9", "羽毛球", VenueTypeEnum.SPORTS.getCode()),
    Sport_10(10, "Sport_10", "高尔夫球", VenueTypeEnum.SPORTS.getCode()),
    Sport_11(11, "Sport_11", "赛车", VenueTypeEnum.SPORTS.getCode()),
    Sport_12(12, "Sport_12", "游泳", VenueTypeEnum.SPORTS.getCode()),
    Sport_13(13, "Sport_13", "政治", VenueTypeEnum.SPORTS.getCode()),
    Sport_14(14, "Sport_14", "水球", VenueTypeEnum.SPORTS.getCode()),
    Sport_15(15, "Sport_15", "跳水", VenueTypeEnum.SPORTS.getCode()),
    Sport_16(16, "Sport_16", "拳击", VenueTypeEnum.SPORTS.getCode()),
    Sport_17(17, "Sport_17", "射箭", VenueTypeEnum.SPORTS.getCode()),
    Sport_18(18, "Sport_18", "乒乓球", VenueTypeEnum.SPORTS.getCode()),
    Sport_19(19, "Sport_19", "举重", VenueTypeEnum.SPORTS.getCode()),

    Sport_20(20, "Sport_20", "皮划艇", VenueTypeEnum.SPORTS.getCode()),
    Sport_21(21, "Sport_21", "体操", VenueTypeEnum.SPORTS.getCode()),
    Sport_22(22, "Sport_22", "田径", VenueTypeEnum.SPORTS.getCode()),
    Sport_23(23, "Sport_23", "马术", VenueTypeEnum.SPORTS.getCode()),
    Sport_24(24, "Sport_24", "手球", VenueTypeEnum.SPORTS.getCode()),
    Sport_25(25, "Sport_25", "飞镖", VenueTypeEnum.SPORTS.getCode()),
    Sport_26(26, "Sport_26", "橄榄球", VenueTypeEnum.SPORTS.getCode()),
    Sport_28(28, "Sport_28", "曲棍球", VenueTypeEnum.SPORTS.getCode()),
    Sport_29(29, "Sport_29", "冬季运动", VenueTypeEnum.SPORTS.getCode()),

    Sport_30(30, "Sport_30", "壁球", VenueTypeEnum.SPORTS.getCode()),
    Sport_31(31, "Sport_31", "娱乐", VenueTypeEnum.SPORTS.getCode()),
    Sport_32(32, "Sport_32", "篮网球", VenueTypeEnum.SPORTS.getCode()),
    Sport_33(33, "Sport_33", "自行车", VenueTypeEnum.SPORTS.getCode()),
    Sport_34(34, "Sport_34", "击剑", VenueTypeEnum.SPORTS.getCode()),
    Sport_35(35, "Sport_35", "柔道", VenueTypeEnum.SPORTS.getCode()),
    Sport_36(36, "Sport_36", "现代五项", VenueTypeEnum.SPORTS.getCode()),
    Sport_37(37, "Sport_37", "划船", VenueTypeEnum.SPORTS.getCode()),
    Sport_38(38, "Sport_38", "帆船", VenueTypeEnum.SPORTS.getCode()),
    Sport_39(39, "Sport_39", "射击", VenueTypeEnum.SPORTS.getCode()),

    Sport_40(40, "Sport_40", "跆拳道", VenueTypeEnum.SPORTS.getCode()),
    Sport_41(41, "Sport_41", "铁人三项", VenueTypeEnum.SPORTS.getCode()),
    Sport_42(42, "Sport_42", "角力", VenueTypeEnum.SPORTS.getCode()),
    Sport_43(43, "Sport_43", "电子竞技", VenueTypeEnum.SPORTS.getCode()),
    Sport_44(44, "Sport_44", "泰拳", VenueTypeEnum.SPORTS.getCode()),
    Sport_45(45, "Sport_45", "沙滩排球", VenueTypeEnum.SPORTS.getCode()),
    Sport_47(47, "Sport_47", "卡巴迪", VenueTypeEnum.SPORTS.getCode()),
    Sport_48(48, "Sport_48", "藤球", VenueTypeEnum.SPORTS.getCode()),
    Sport_49(49, "Sport_49", "室内足球", VenueTypeEnum.SPORTS.getCode()),

    Sport_50(50, "Sport_50", "板球", VenueTypeEnum.SPORTS.getCode()),
    Sport_51(51, "Sport_51", "沙滩足球", VenueTypeEnum.SPORTS.getCode()),
    Sport_52(52, "Sport_52", "扑克", VenueTypeEnum.SPORTS.getCode()),
    Sport_53(53, "Sport_53", "国际象棋", VenueTypeEnum.SPORTS.getCode()),
    Sport_54(54, "Sport_54", "奥林匹克", VenueTypeEnum.SPORTS.getCode()),
    Sport_55(55, "Sport_55", "金融", VenueTypeEnum.SPORTS.getCode()),
    Sport_56(56, "Sport_56", "乐透", VenueTypeEnum.SPORTS.getCode()),
    Sport_99(99, "Sport_99", "其他", VenueTypeEnum.SPORTS.getCode()),

    Sport_154(154, "Sport_154", "赛马固定赔率", null),
    Sport_157(157, "Sport_157", "玩家打赏", VenueTypeEnum.SPORTS.getCode()),

    Sport_161(161, "Sport_161", "百练赛", VenueTypeEnum.ACELT.getCode()),
    Sport_164(164, "Sport_164", "快乐 5", VenueTypeEnum.ACELT.getCode()),

    Sport_165(165, "Sport_165", "乐卡迪亚", null),
    Sport_167(167, "Sport_167", "CG 电子", null),
    Sport_168(168, "Sport_168", "快乐 5 系列游戏", VenueTypeEnum.ACELT.getCode()),
    Sport_169(169, "Sport_169", "体育时时彩", VenueTypeEnum.ACELT.getCode()),
    Sport_147(147, "Sport_147", "Virtually 足球", VenueTypeEnum.ACELT.getCode()),

    Sport_171(171, "Sport_171", "Vgaming", null),
    Sport_172(172, "Sport_172", "AdvantPlay", null),
    Sport_174(174, "Sport_174", "AdvantPlay Mini", null),
    Sport_175(175, "Sport_175", "比特币", VenueTypeEnum.SPORTS.getCode()),
    Sport_176(176, "Sport_176", "正博娱乐", null),
    Sport_179(179, "虚拟足球联", "Virtual Sports", null),
    Sport_180(180, "Sport_180", "虚拟足球", VenueTypeEnum.SPORTS.getCode()),
    Sport_181(181, "Sport_181", "虚拟赛马", VenueTypeEnum.SPORTS.getCode()),
    Sport_182(182, "Sport_182", "虚拟赛狗", VenueTypeEnum.SPORTS.getCode()),
    Sport_183(183, "Sport_183", "虚拟沙地摩托车", VenueTypeEnum.SPORTS.getCode()),
    Sport_184(184, "Sport_184", "虚拟赛车", VenueTypeEnum.SPORTS.getCode()),
    Sport_185(185, "Sport_185", "虚拟自行车", VenueTypeEnum.SPORTS.getCode()),
    Sport_186(186, "Sport_186", "虚拟网球", VenueTypeEnum.SPORTS.getCode()),

    Sport_190(190, "Sport_190", "虚拟足球联赛", VenueTypeEnum.SPORTS.getCode()),
    Sport_191(191, "Sport_191", "虚拟足球国家杯", VenueTypeEnum.SPORTS.getCode()),
    Sport_192(192, "Sport_192", "虚拟足球世界杯", VenueTypeEnum.SPORTS.getCode()),
    Sport_193(193, "Sport_193", "虚拟篮球", VenueTypeEnum.SPORTS.getCode()),
    Sport_194(194, "Sport_194", "虚拟足球亚洲杯", VenueTypeEnum.SPORTS.getCode()),
    Sport_195(195, "Sport_195", "Virtual Soccer English Premier", VenueTypeEnum.SPORTS.getCode()),
    Sport_196(196, "Sport_196", "虚拟足球冠军杯", VenueTypeEnum.SPORTS.getCode()),
    Sport_197(197, "Sport_197", "虚拟足球欧洲杯", VenueTypeEnum.SPORTS.getCode()),

    Sport_202(202, "Sport_202", "快乐彩", null),
    Sport_208(208, "Sport_208", "沙巴娱乐城", null),
    Sport_209(209, "Sport_209", "沙巴迷你游戏", null), // 沙巴娱乐城 - 迷你游戏

    Sport_211(211, "Sport_211", "欧博", null),
    Sport_212(212, "Sport_212", "澳门电子娱乐城", null),

    Sport_220(220, "Sport_220", "彩票", null),
    Sport_222(222, "Sport_222", "桌面游戏", null),
    Sport_223(223, "Sport_223", "Togel 4D", null),

    Sport_232(232, "Sport_232", "PG Soft", null),
    Sport_233(233, "Sport_233", "完美真人", null),
    Sport_234(234, "Sport_234", "Joker", null),
    Sport_237(237, "Sport_237", "MaxGame", null),
    Sport_238(238, "Sport_238", "HB 电子", null),

    Sport_243(243, "Sport_243", "AE 性感真人", null),
    Sport_244(244, "Sport_244", "SA Gaming", null),
    Sport_245(245, "Sport_245", "虚拟娱乐城", null),
    Sport_247(247, "Sport_247", "新霸电子", null),
    Sport_248(248, "Sport_248", "PP 王者电子", null),
    VIRTUALLY(147, "Virtually", "足球", null);

    /**
     * 虚拟赛事
     */
    public static List<SBSportTypeEnum> getVirtualList() {
        return Lists.newArrayList(SBSportTypeEnum.Sport_179, SBSportTypeEnum.Sport_180, SBSportTypeEnum.Sport_181,
                SBSportTypeEnum.Sport_182, SBSportTypeEnum.Sport_183, SBSportTypeEnum.Sport_184,
                SBSportTypeEnum.Sport_185, SBSportTypeEnum.Sport_186, SBSportTypeEnum.Sport_190,
                SBSportTypeEnum.Sport_191, SBSportTypeEnum.Sport_192, SBSportTypeEnum.Sport_193
                , SBSportTypeEnum.Sport_194, SBSportTypeEnum.Sport_195, SBSportTypeEnum.Sport_196, SBSportTypeEnum.Sport_197);
    }

    public static String getVirtualName(Integer sportType) {
        List<SBSportTypeEnum> list = getVirtualList();
        SBSportTypeEnum sbSportTypeEnum = SBSportTypeEnum.getEnumById(sportType);

        if (list.contains(sbSportTypeEnum)) {
            return "虚拟赛事";
        }
        return "正常赛事";
    }



    /**
     * 足球赛事
     */
    public static List<SBSportTypeEnum> footballList() {
        return Lists.newArrayList(
                SBSportTypeEnum.Sport_1
                ,SBSportTypeEnum.Sport_147
                ,SBSportTypeEnum.Sport_179
                , SBSportTypeEnum.Sport_180
                , SBSportTypeEnum.Sport_190
                , SBSportTypeEnum.Sport_191
                , SBSportTypeEnum.Sport_192
                , SBSportTypeEnum.Sport_194
                , SBSportTypeEnum.Sport_196
                , SBSportTypeEnum.Sport_197
        );
    }


    /**
     * 推荐足球赛事
     */
    public static List<SBSportTypeEnum> footballSportTypeEnumList() {
        return Lists.newArrayList(
                SBSportTypeEnum.Sport_1
                , SBSportTypeEnum.Sport_180
                , SBSportTypeEnum.Sport_190
                , SBSportTypeEnum.Sport_191
                , SBSportTypeEnum.Sport_192
                , SBSportTypeEnum.Sport_194
                , SBSportTypeEnum.Sport_196
                , SBSportTypeEnum.Sport_197
        );
    }


    /**
     * 篮球赛事
     */
    public static List<SBSportTypeEnum> basketballSportTypeEnumList() {
        return Lists.newArrayList(SBSportTypeEnum.Sport_2,
                SBSportTypeEnum.Sport_193
                , SBSportTypeEnum.Sport_193
        );
    }


    /**
     * 体育种类id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String name;

    /**
     * 体育类型
     */
    private Integer gameType;

    /**
     * 游戏类型
     */
    // private Integer gameType;
    public static Integer gameTypeOfId(Integer id) {
        if (id == null) {
            return null;
        }

        for (SBSportTypeEnum sportTypeEnums : SBSportTypeEnum.values()) {
            if (sportTypeEnums.getId().equals(id)) {
                return sportTypeEnums.getGameType();
            }
        }
        return null;
    }

    /**
     * 游戏类型
     */
    // private Integer gameType;
    public static String nameOfId(Integer id) {
        if (id == null) {
            return "";
        }

        for (SBSportTypeEnum sportTypeEnums : SBSportTypeEnum.values()) {
            if (sportTypeEnums.getId().equals(id)) {
                return sportTypeEnums.getName();
            }
        }
        return "";
    }

    public static SBSportTypeEnum getEnumById(Integer id) {
        if (id == null) {
            return null;
        }

        for (SBSportTypeEnum sportTypeEnums : SBSportTypeEnum.values()) {
            if (sportTypeEnums.getId().equals(id)) {
                return sportTypeEnums;
            }
        }
        return null;
    }
}
