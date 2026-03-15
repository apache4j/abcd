package com.cloud.baowang.play.api.enums.venue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

@Getter
@AllArgsConstructor
public enum VenueEnum {
//    SH(VenuePlatformConstants.SH, "SH", "WinTo真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//    SH_ZHCN(VenuePlatformConstants.SH, "SH_ZHCN", "WinTo国内盘真人", VenueTypeEnum.SH, VenueJoinTypeEnum.VENUE),
//    SA(VenuePlatformConstants.SH, "SA", "SA真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//    PG(VenuePlatformConstants.SH, "PG", "PG平台", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    ACELT(VenuePlatformConstants.SH, "ACELT", "彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
//    WP_ACELT(VenuePlatformConstants.SH, "WP_ACELT", "王牌彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
//    WP_ACELT_02(VenuePlatformConstants.SH, "WP_ACELT_02", "王牌彩票_02", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
//    WP_ACELT_03(VenuePlatformConstants.SH, "WP_ACELT_03", "王牌彩票_03", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
//    WP_ACELT_OK(VenuePlatformConstants.SH, "WP_ACELT_OK", "王牌彩票_OK", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
//    WP_ACELT_ZHCN(VenuePlatformConstants.SH, "WP_ACELT_ZHCN", "王牌彩票_ZHCN", VenueTypeEnum.ACELT, VenueJoinTypeEnum.VENUE),
//    SBA(VenuePlatformConstants.SH, "SBA", "沙巴体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.DATA_SOURCE),
//    JILI("JILI", "JILI", "吉利电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    TADA("TADA", "TADA", "TADA电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    S128("S128", "S128", "S128斗鸡", VenueTypeEnum.COCKFIGHTING, VenueJoinTypeEnum.VENUE),
//    TF("TF", "TF", "TF电竞", VenueTypeEnum.ELECTRONIC_SPORTS, VenueJoinTypeEnum.VENUE),
//    LGD("LGD", "LGD", "LGD电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    IM("IM", "IM", "IM电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    JILIPLUS("JILIPLUS", "JILIPLUS", "jili电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PGPLUS("PGPLUS", "PGPLUS", "pg电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PPPLUS("PPPLUS", "PPPLUS", "pp电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    MARBLES("MARBLES", "MARBLES", "弹珠游戏(IM)", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),
//    FTG("FTG", "FTG", "FTG(捕鱼)", VenueTypeEnum.FISHING, VenueJoinTypeEnum.GAME),
//    JILIPLUS_02("JILIPLUS_02", "JILIPLUS_02", "jili电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PGPLUS_02("PGPLUS_02", "PGPLUS_02", "pg电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PPPLUS_02("PPPLUS_02", "PPPLUS_02", "pp电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    CQ9("CQ9", "CQ9", "CQ9平台", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    CQ9_TEST("CQ9_TEST", "CQ9_TEST", "CQ9_TEST", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PG_TEST("PG_TEST", "PG_TEST", "PG_TEST平台", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    V8("V8", "V8", "V8", VenueTypeEnum.CHESS, VenueJoinTypeEnum.GAME),
//
//    JILI_03("JILI_03", "JILI_03", "JILI电子(v3)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    JILIPLUS_03("JILIPLUS_03", "JILIPLUS_03", "jili电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PGPLUS_03("PGPLUS_03", "PGPLUS_03", "pg电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PPPLUS_03("PPPLUS_03", "PPPLUS_03", "pp电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    PP("PP", "PP", "pp电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    FC("FC", "FC", "FC电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    JDB("JDB", "JDB", "JDB电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    NEXTSPIN("NEXTSPIN", "NEXTSPIN", "NEXTSPIN", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    FASTSPIN("FASTSPIN", "FASTSPIN", "FastSpin电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    ACE("ACE", "ACE", "ACE电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    CMD("CMD", "CMD", "CMD体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),
//
//    SEXY("SEXYBCRT", "SEXY", "SEXY真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//    EVO("EVO", "EVO", "EVO真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//
//
//    PLAYTECH_SH("PLAYTECH_SH", "PLAYTECH_SH", "PLAYTECH真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//    PLAYTECH_EG("PLAYTECH_EG", "PLAYTECH_EG", "PLAYTECH电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    DG2("DG2", "DG2", "DG2真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
//    BTI("BTI", "BTI", "BTI体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),
//    SPADE("SPADE", "SPADE", "SPADE电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//    DB_PANDA_SPORT("DB_PANDA_SPORT", "DB_PANDA_SPORT", "DB熊猫体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),
//
//    DBEVG("DBEVG", "DBEVG", "DB电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
//
//    DBFISHING("DBFISHING", "DBFISHING", "DB捕鱼", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),
//    DB_DJ("DB_DJ", "DB_DJ", "多宝电竞", VenueTypeEnum.ELECTRONIC_SPORTS, VenueJoinTypeEnum.VENUE),
//
//    DBCHESS("DBCHESS", "DBCHESS", "博雅棋牌", VenueTypeEnum.CHESS, VenueJoinTypeEnum.VENUE),
//
//    DBACELT("DBACELT", "DBACELT", "DB彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.VENUE),
//
//    DBSH("DBSH", "DBSH", "DB真人", VenueTypeEnum.SH, VenueJoinTypeEnum.VENUE),
//
//    DBSCRATCH("DBSCRATCH", "DBSCRATCH", "DB刮刮乐", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),



    SH(VenuePlatformConstants.SH, "SH", "WinTo真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    SH_ZHCN(VenuePlatformConstants.SH, "SH_ZHCN", "WinTo国内盘真人", VenueTypeEnum.SH, VenueJoinTypeEnum.VENUE),
    SA(VenuePlatformConstants.SA, "SA", "SA真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    PG(VenuePlatformConstants.PG, "PG", "PG平台", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    ACELT(VenuePlatformConstants.ACELT, "ACELT", "彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
    WP_ACELT(VenuePlatformConstants.WP_ACELT, "WP_ACELT", "王牌彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
    WP_ACELT_02(VenuePlatformConstants.WP_ACELT, "WP_ACELT_02", "王牌彩票_02", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
    WP_ACELT_03(VenuePlatformConstants.WP_ACELT, "WP_ACELT_03", "王牌彩票_03", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
    WP_ACELT_OK(VenuePlatformConstants.WP_ACELT, "WP_ACELT_OK", "王牌彩票_OK", VenueTypeEnum.ACELT, VenueJoinTypeEnum.DATA_SOURCE),
    WP_ACELT_ZHCN(VenuePlatformConstants.WP_ACELT, "WP_ACELT_ZHCN", "王牌彩票_ZHCN", VenueTypeEnum.ACELT, VenueJoinTypeEnum.VENUE),
    SBA(VenuePlatformConstants.SBA, "SBA", "沙巴体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.DATA_SOURCE),

    JILI(VenuePlatformConstants.JILI, "JILI", "吉利电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    TADA(VenuePlatformConstants.TADA, "TADA", "TADA电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    S128(VenuePlatformConstants.S128, "S128", "S128斗鸡", VenueTypeEnum.COCKFIGHTING, VenueJoinTypeEnum.VENUE),
    TF(VenuePlatformConstants.TF, "TF", "TF电竞", VenueTypeEnum.ELECTRONIC_SPORTS, VenueJoinTypeEnum.VENUE),
    LGD(VenuePlatformConstants.LGD, "LGD", "LGD电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    IM(VenuePlatformConstants.IM, "IM", "IM电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    JILIPLUS(VenuePlatformConstants.JILIPLUS, "JILIPLUS", "jili电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PGPLUS(VenuePlatformConstants.PGPLUS, "PGPLUS", "pg电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PPPLUS(VenuePlatformConstants.PPPLUS, "PPPLUS", "pp电子(plus)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),

    MARBLES(VenuePlatformConstants.MARBLES, "MARBLES", "弹珠游戏(IM)", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),
    FTG(VenuePlatformConstants.FTG, "FTG", "FTG(捕鱼)", VenueTypeEnum.FISHING, VenueJoinTypeEnum.GAME),
    JILIPLUS_02(VenuePlatformConstants.JILIPLUS, "JILIPLUS_02", "jili电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PGPLUS_02(VenuePlatformConstants.PGPLUS, "PGPLUS_02", "pg电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PPPLUS_02(VenuePlatformConstants.PPPLUS, "PPPLUS_02", "pp电子(plus_02)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),

    CQ9(VenuePlatformConstants.CQ9, "CQ9", "CQ9平台", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),

    V8(VenuePlatformConstants.V8, "V8", "V8", VenueTypeEnum.CHESS, VenueJoinTypeEnum.GAME),
    JILI_03(VenuePlatformConstants.NEW_JILI, "JILI_03", "JILI电子(v3)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    JILIPLUS_03(VenuePlatformConstants.JILIPLUS, "JILIPLUS_03", "jili电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PGPLUS_03(VenuePlatformConstants.PGPLUS, "PGPLUS_03", "pg电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PPPLUS_03(VenuePlatformConstants.PPPLUS, "PPPLUS_03", "pp电子(plus_03)", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    PP(VenuePlatformConstants.PP, "PP", "pp电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    FC(VenuePlatformConstants.FC, "FC", "FC电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),

    JDB(VenuePlatformConstants.JDB, "JDB", "JDB电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    NEXTSPIN(VenuePlatformConstants.NEXTSPIN, "NEXTSPIN", "NEXTSPIN", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    FASTSPIN(VenuePlatformConstants.FASTSPIN, "FASTSPIN", "FastSpin电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    ACE(VenuePlatformConstants.ACE, "ACE", "ACE电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    CMD(VenuePlatformConstants.CMD, "CMD", "CMD体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),

    SEXY(VenuePlatformConstants.SEXY, "SEXY", "SEXY真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    EVO(VenuePlatformConstants.EVO, "EVO", "EVO真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    PLAYTECH_SH(VenuePlatformConstants.PLAYTECH_SH, "PLAYTECH_SH", "PLAYTECH真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    PLAYTECH_EG(VenuePlatformConstants.PLAYTECH_EG, "PLAYTECH_EG", "PLAYTECH电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    DG2(VenuePlatformConstants.DG2, "DG2", "DG2真人", VenueTypeEnum.SH, VenueJoinTypeEnum.GAME),
    BTI(VenuePlatformConstants.BTI, "BTI", "BTI体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),
    SPADE(VenuePlatformConstants.SPADE, "SPADE", "SPADE电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    DB_PANDA_SPORT(VenuePlatformConstants.DB_PANDA_SPORT, "DB_PANDA_SPORT", "DB熊猫体育", VenueTypeEnum.SPORTS, VenueJoinTypeEnum.VENUE),

    DBEVG(VenuePlatformConstants.DBEVG, "DBEVG", "DB电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    DBFISHING(VenuePlatformConstants.DBFISHING, "DBFISHING", "DB捕鱼", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),
    DB_DJ(VenuePlatformConstants.DB_DJ, "DB_DJ", "多宝电竞", VenueTypeEnum.ELECTRONIC_SPORTS, VenueJoinTypeEnum.VENUE),
    DBCHESS(VenuePlatformConstants.DBCHESS, "DBCHESS", "博雅棋牌", VenueTypeEnum.CHESS, VenueJoinTypeEnum.VENUE),
    DBACELT(VenuePlatformConstants.DBACELT, "DBACELT", "DB彩票", VenueTypeEnum.ACELT, VenueJoinTypeEnum.VENUE),
    DBSH(VenuePlatformConstants.DBSH, "DBSH", "DB真人", VenueTypeEnum.SH, VenueJoinTypeEnum.VENUE),
    DBSCRATCH(VenuePlatformConstants.DBSCRATCH, "DBSCRATCH", "DB刮刮乐", VenueTypeEnum.MARBLES, VenueJoinTypeEnum.GAME),

    WINTO_EVG("WINTOEVG", "WINTOEVG", "winto电子", VenueTypeEnum.ELECTRONICS, VenueJoinTypeEnum.GAME),
    ;

    private String venuePlatform;
    private String venueCode;
    private String venueName;
    private VenueTypeEnum type; // 游戏大类
    private VenueJoinTypeEnum venueJoinTypeEnum;

    public static String getPlatformByCode(String venueCode) {
        if (null == venueCode) {
            return null;
        }
        VenueEnum[] types = VenueEnum.values();
        for (VenueEnum type : types) {
            if (venueCode.equals(type.getVenueCode())) {
                return type.getVenuePlatform();
            }
        }
        return null;
    }

    public static VenueEnum nameOfCode(String venueCode) {
        if (null == venueCode) {
            return null;
        }
        VenueEnum[] types = VenueEnum.values();
        for (VenueEnum type : types) {
            if (venueCode.equals(type.getVenueCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<Map<String, String>> getMapList() {
        List<Map<String, String>> list = new ArrayList<>(4);
        for (VenueEnum venueEnum : VenueEnum.values()) {
            Map<String, String> map = new HashMap<String, String>(4);
            map.put("venueCode", venueEnum.getVenueCode());
            map.put("venueName", venueEnum.getVenueName());
            list.add(map);
        }
        return list;
    }

    public static List<String> getVenueCodeList() {
        List<String> list = new ArrayList<>();
        for (VenueEnum venueEnum : VenueEnum.values()) {
            list.add(venueEnum.getVenueCode());
        }
        return list;
    }


    /**
     * 获取单场馆的体育场馆列表
     */
    public static List<String> getSignSportVenueCodeList(){
        List<String> list = Lists.newArrayList();

        for (VenueEnum venueEnum : VenueEnum.values()) {
            if(VenueJoinTypeEnum.VENUE.equals(venueEnum.getVenueJoinTypeEnum()) && VenueTypeEnum.SPORTS.equals(venueEnum.getType())){
                list.add(venueEnum.getVenueCode());
            }
        }
        return list;
    }


    /**
     * 获取电竞后台,除了TF调用三方接口的
     */
    public static List<String> getESportVenueCodeList(){
        List<String> list = Lists.newArrayList();

        for (VenueEnum venueEnum : VenueEnum.values()) {
            if(VenueTypeEnum.ELECTRONIC_SPORTS.equals(venueEnum.getType()) && !VenueEnum.TF.equals(venueEnum)){
                list.add(venueEnum.getVenueCode());
            }
        }
        return list;
    }


    public static List<String> getPlatformList() {
        Set<String> set = new HashSet<>();
        for (VenueEnum venueEnum : VenueEnum.values()) {
            set.add(venueEnum.getVenuePlatform());
        }
        return set.stream().toList();
    }

    public static List<VenueEnum> getVenueCodeByPlatform(String venuePlatform) {
        List<VenueEnum> list = Lists.newArrayList();
        VenueEnum[] types = VenueEnum.values();
        for (VenueEnum type : types) {
            if (venuePlatform.equals(type.getVenuePlatform())) {
                list.add(type);
            }
        }
        return list;
    }

    public static String getVenueNameByCode(String venueCode) {
        VenueEnum[] types = VenueEnum.values();
        for (VenueEnum type : types) {
            if (venueCode.equals(type.getVenueCode())) {
                return type.getVenueName();
            }
        }

        return null;
    }

    public static VenueEnum of(String venueCode) {
        VenueEnum[] types = VenueEnum.values();
        for (VenueEnum type : types) {
            if (venueCode.equals(type.getVenueCode())) {
                return type;
            }
        }

        return null;
    }


}
