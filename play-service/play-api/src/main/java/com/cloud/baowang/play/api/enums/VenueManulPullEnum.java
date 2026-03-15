package com.cloud.baowang.play.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant.*;

@AllArgsConstructor
@Getter
public enum VenueManulPullEnum {
    SH(SH_GAME_PULL_BET_TASK, "SH"),
    SH_ZHCN(SH_ZHCN_GAME_PULL_BET_TASK, "SH_ZHCN"),
    TF(TF_GAME_PULL_BET_TASK, "TF"),
    S128(S128_GAME_PULL_BET_TASK, "S128"),
    JILIPLUS(JILI_PLUS_GAME_PULL_BET_TASK, "JILIPLUS"),
    PGPLUS(PG_PLUS_GAME_PULL_BET_TASK, "PGPLUS"),
    PPPLUS(PP_PLUS_GAME_PULL_BET_TASK, "PPPLUS"),
    V8(V8_GAME_PULL_BET_TASK, "V8"),
    MARBLES(MARBLES_GAME_PULL_BET_TASK, "MARBLES"),
    FTG(FTG_GAME_PULL_BET_TASK, "FTG"),
    ACELT(ACE_LT_GAME_PULL_BET_TASK, "ACELT"),
    WP_ACELT(WP_ACE_LT_GAME_PULL_BET_TASK, "WP_ACELT"),
    WP_ACELT_02(WP_ACE_LT_02_GAME_PULL_BET_TASK, "WP_ACELT_02"),
    WP_ACELT_03(WP_ACE_LT_03_GAME_PULL_BET_TASK, "WP_ACELT_03"),
    WP_ACELT_OK(WP_ACE_LT_OK_GAME_PULL_BET_TASK, "WP_ACELT_OK"),
    WP_ACELT_ZHCN(WP_ACE_LT_ZH_CN_GAME_PULL_BET_TASK, "WP_ACELT_ZHCN"),
    LGD(LGD_GAME_PULL_BET_TASK, "LGD"),
    CQ9(CQ9_GAME_PULL_BET_TASK, "CQ9"),
    PP(PP_GAME_PULL_BET_TASK, "PP"),
    FC(FC_GAME_PULL_BET_TASK, "FC"),
    EVO(EVO_GAME_PULL_BET_TASK, "EVO"),
    SA(SA_GAME_PULL_BET_TASK, "SA"),
    DB_DJ(DB_DJ_GAME_PULL_BET_TASK, "DB_DJ"),
    DB_PANDA_SPORT(DB_PANDA_SPORT_GAME_PULL_BET_TASK, "DB_PANDA_SPORT"),

    // sba 暂不支持
    // SBA(SBA_GAME_PULL_BET_TASK, "SBA"),
    // PG(PG_GAME_PULL_BET_TASK, "PG"),


    JILIPLUS_02(JILI_PLUS_GAME_PULL_BET_TASK_02, "JILIPLUS_02"),
    PGPLUS_02(PP_PLUS_GAME_PULL_BET_TASK_02, "PGPLUS_02"),
    PPPLUS_02(PG_PLUS_GAME_PULL_BET_TASK_02, "PPPLUS_02"),
    IM(IM_GAME_PULL_BET_TASK, "IM"),
    JILI_03(JILI_03_GAME_PULL_BET_TASK, "JILI_03"),

    JILIPLUS_03(JILI_PLUS_GAME_PULL_BET_TASK_03, "JILIPLUS_03"),
    PGPLUS_03(PP_PLUS_GAME_PULL_BET_TASK_03, "PGPLUS_03"),
    PPPLUS_03(PG_PLUS_GAME_PULL_BET_TASK_03, "PPPLUS_03"),
    NEXTSPIN(NEXTSPIN_GAME_PULL_BET_TASK, "NEXTSPIN"),
    JDB(JDB_GAME_PULL_BET_TASK,"JDB"),
    FASTSPIN(FAST_SPIN_GAME_PULL_BET_TASK, "FASTSPIN"),
    CND(CMD_GAME_PULL_BET_TASK, "CMD"),
    DG2(DG2_GAME_PULL_BET_TASK,"DG2"),
    ACE(ACE_GAME_PULL_BET_TASK,"ACE"),
    SPADE(SPADE_GAME_PULL_BET_TASK,"SPADE"),

    SEXY(SEXY_GAME_PULL_BET_TASK,"SEXY"),

    DBEVG(DB_EVG_GAME_PULL_BET_TASK,"DBEVG"),

    DBFISHING(DB_FISHING_GAME_PULL_BET_TASK,"DBFISHING"),

    DBCHESS(DB_CHESS_GAME_PULL_BET_TASK,"DBCHESS"),

    DBACELT(DB_ACELT_GAME_PULL_BET_TASK,"DBACELT"),

    DBSH(DB_SH_GAME_PULL_BET_TASK,"DBSH"),


    DBSCRATCH(DB_SCRATCH_LOTTERY_GAME_PULL_BET_TASK,"DBSCRATCH"),


    WINTOEVG(WINTO_EVG_GAME_PULL_BET_TASK,"WINTOEVG"),
    ;


    private String taskType;
    private String venueCode;



    public static List<String> getPullTypeByVenueCode(String venueCode) {
        // im不支持手动拉单
        if (venueCode == null || venueCode.equalsIgnoreCase("IM")){
            return null;
        }
        List<String> ret = new ArrayList<>();
        VenueManulPullEnum[] types = VenueManulPullEnum.values();
        for (VenueManulPullEnum type : types) {
            if (venueCode.equals(type.getVenueCode())) {
                ret.add(type.getTaskType());
            }
        }
        return  ret;
    }



}
