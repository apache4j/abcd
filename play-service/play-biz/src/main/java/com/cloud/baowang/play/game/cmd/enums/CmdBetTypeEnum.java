package com.cloud.baowang.play.game.cmd.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
@Getter
@AllArgsConstructor
public enum CmdBetTypeEnum {

    SBA_BET_TYPE_1("1", "1X2","1X2",CmdBetInfoEnum.SBA_BET_TYPE_1X2,CmdBetInfoCNEnum.SBA_BET_TYPE_1X2),
    SBA_BET_TYPE_2("2", "1X2","1X2",CmdBetInfoEnum.SBA_BET_TYPE_1X2,CmdBetInfoCNEnum.SBA_BET_TYPE_1X2),
    SBA_BET_TYPE_1X2("1X2", "1X2","1X2",CmdBetInfoEnum.SBA_BET_TYPE_1X2,CmdBetInfoCNEnum.SBA_BET_TYPE_1X2),
    SBA_BET_TYPE_CS("CS", "Correct Score","波胆",CmdBetInfoEnum.SBA_BET_TYPE_CS,CmdBetInfoCNEnum.SBA_BET_TYPE_CS),
    SBA_BET_TYPE_FLG("FLG", "First/Last","最先进球/最后进球",CmdBetInfoEnum.SBA_BET_TYPE_FLG,CmdBetInfoCNEnum.SBA_BET_TYPE_FLG),
    SBA_BET_TYPE_HDP("HDP", "Handicap","让球",CmdBetInfoEnum.SBA_BET_TYPE_HDP,CmdBetInfoCNEnum.SBA_BET_TYPE_HDP),
    SBA_BET_TYPE_HFT("HFT", "Half/Full","半场/全场",CmdBetInfoEnum.SBA_BET_TYPE_HFT,CmdBetInfoCNEnum.SBA_BET_TYPE_HFT),
    SBA_BET_TYPE_OE("OE", "Odd/Even","单/双",CmdBetInfoEnum.SBA_BET_TYPE_OE,CmdBetInfoCNEnum.SBA_BET_TYPE_OE),
    SBA_BET_TYPE_OU("OU", "Over/Under","大/小 ",CmdBetInfoEnum.SBA_BET_TYPE_OU,CmdBetInfoCNEnum.SBA_BET_TYPE_OU),
    SBA_BET_TYPE_OUT("OUT", "Outright","优胜冠军",CmdBetInfoEnum.SBA_BET_TYPE_OUT,CmdBetInfoCNEnum.SBA_BET_TYPE_OUT),
    SBA_BET_TYPE_PAR("PAR", "Mixed Parlay","混合过关",CmdBetInfoEnum.SBA_BET_TYPE_PAR,CmdBetInfoCNEnum.SBA_BET_TYPE_PAR),
    SBA_BET_TYPE_TG("TG", "Total Goals","总入球",CmdBetInfoEnum.SBA_BET_TYPE_TG,CmdBetInfoCNEnum.SBA_BET_TYPE_TG),
    SBA_BET_TYPE_TG1H("TG1H", "Total Goals","总入球",CmdBetInfoEnum.SBA_BET_TYPE_TG1H,CmdBetInfoCNEnum.SBA_BET_TYPE_TG1H),
    SBA_BET_TYPE_X("X", "1X2","1X2",CmdBetInfoEnum.SBA_BET_TYPE_1X2,CmdBetInfoCNEnum.SBA_BET_TYPE_1X2),
    SBA_BET_TYPE_DC("DC", "Double Chance","双重机会",CmdBetInfoEnum.SBA_BET_TYPE_DC,CmdBetInfoCNEnum.SBA_BET_TYPE_DC),
    SBA_BET_TYPE_1X("1X", "Double Chance","双重机会",CmdBetInfoEnum.SBA_BET_TYPE_DC,CmdBetInfoCNEnum.SBA_BET_TYPE_DC),
    SBA_BET_TYPE_12("12", "Double Chance","双重机会",CmdBetInfoEnum.SBA_BET_TYPE_DC,CmdBetInfoCNEnum.SBA_BET_TYPE_DC),
    SBA_BET_TYPE_X2("X2", "Double Chance","双重机会",CmdBetInfoEnum.SBA_BET_TYPE_DC,CmdBetInfoCNEnum.SBA_BET_TYPE_DC),
    SBA_BET_TYPE_ETG("ETG", "Exact Total Goals","全场准确总进球",CmdBetInfoEnum.SBA_BET_TYPE_ETG,CmdBetInfoCNEnum.SBA_BET_TYPE_ETG),
    SBA_BET_TYPE_HTG("HTG", "Home Exact Goals","主队准确总进球",CmdBetInfoEnum.SBA_BET_TYPE_HTG,CmdBetInfoCNEnum.SBA_BET_TYPE_HTG),
    SBA_BET_TYPE_ATG("ATG", "Away Exact Goals","客队准确总进球",CmdBetInfoEnum.SBA_BET_TYPE_ATG,CmdBetInfoCNEnum.SBA_BET_TYPE_ATG),
    SBA_BET_TYPE_HP3("HP3", "3-Way Handicap","三项让分投注",CmdBetInfoEnum.SBA_BET_TYPE_HP3,CmdBetInfoCNEnum.SBA_BET_TYPE_HP3),
    SBA_BET_TYPE_CNS("CNS", "Clean Sheet","零失球",CmdBetInfoEnum.SBA_BET_TYPE_CNS,CmdBetInfoCNEnum.SBA_BET_TYPE_CNS);
    private String code;
    private String desc;
    private String descCn;
    private Map<String,String> MapValue;
    private Map<String,String> MapCnValue;

    public static CmdBetTypeEnum of(String code,Boolean isFirstHalf) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (CmdBetTypeEnum obj : CmdBetTypeEnum.values()) {
            if (CmdBetTypeEnum.SBA_BET_TYPE_TG.getCode().equals(code)){
                if (ObjectUtil.isNotEmpty(isFirstHalf) && isFirstHalf){
                    return CmdBetTypeEnum.SBA_BET_TYPE_TG1H;
                }
            }
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }

}
