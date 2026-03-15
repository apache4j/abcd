package com.cloud.baowang.user.enums;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.google.common.collect.ImmutableList;

/**
 * @Author 小智
 * @Date 6/5/23 4:45 PM
 * @Version 1.0
 */
public enum ChangeOperationEnum {

    VIP_RANK("0", ImmutableList.of(new CodeValueVO("", "dailyWithdrawals", "单日提现次数"),
            new CodeValueVO("", "dayWithdrawLimit", "单日提款上限"),
            new CodeValueVO("", "withdrawFee", "提款手续费"),
            new CodeValueVO("", "vipGradeCodes", "VIP等级"),
            new CodeValueVO("", "vipIcon", "段位图标"),
            new CodeValueVO("", "remark", "备注"))),


    VIP_GRADE("1", ImmutableList.of(new CodeValueVO("", "vipGradeCode", "VIP等级"),
//            new SystemParamVO("", "depositUpgrade", "累计存款升级条件"),
            new CodeValueVO("", "upgradeBonus", "晋级礼金"),
            new CodeValueVO("", "vipRankCode", "所属段位"),
            new CodeValueVO("", "upgradeXp", "升级条件 所需XP"))),

    VIP_BENEFIT("2", ImmutableList.of(
            new CodeValueVO("","weekMinBetAmount", "周奖励最低流水"),
            new CodeValueVO("", "weekRebate", "周返奖比例"),
            new CodeValueVO("", "weekBetMultiple", "周流水倍数"),
            new CodeValueVO("", "monthMinBetAmount", "月奖励最低流水"),
            new CodeValueVO("", "monthRebate", "月返奖比例"),
            new CodeValueVO("", "monthBetMultiple", "月流水倍数"),
            new CodeValueVO("", "weekSportMinBet", "周体育最低流水"),
            new CodeValueVO("", "weekSportRebate", "周体育奖金"),
            new CodeValueVO("", "weekSportMultiple", "周体育流水倍数"),
            new CodeValueVO("", "luckTime", "转盘次数"),
            new CodeValueVO("", "upgrade", "晋级奖金"))),
    ;
//    VIP_PROMOTION("2", ImmutableList.of(new CodeValueVO("", "minTransferAmount", "最低转账额度"),
//            new CodeValueVO("", "dividendBili", "红利比例"),
//            new CodeValueVO("", "topPrize", "最高奖金"),
//            new CodeValueVO("", "multiple", "流水倍数"),
//            new CodeValueVO("", "joinTimes", "参与次数"),
//            new CodeValueVO("", "joinVenue", "参与场馆"))),

//    VIP_REBATE("VIP_REBATE", ImmutableList.of(new CodeValueVO("","rebateVip0", "VIP0级"),
//            new CodeValueVO("", "rebateVip1", "VIP1级"),
//            new CodeValueVO("", "rebateVip2", "VIP2级"),
//            new CodeValueVO("","rebateVip3", "VIP3级"),
//            new CodeValueVO("","rebateVip4", "VIP4级"),
//            new CodeValueVO("", "rebateVip5", "VIP5级"),
//            new CodeValueVO("", "rebateVip6", "VIP6级"),
//            new CodeValueVO("","rebateVip7", "VIP7级"),
//            new CodeValueVO("", "rebateVip8", "VIP8级"),
//            new CodeValueVO("","rebateVip9", "VIP9级"),
//            new CodeValueVO("","rebateVip10", "VIP10级")));
    private final String code;

    private final ImmutableList<CodeValueVO> list;

    ChangeOperationEnum(String code, ImmutableList<CodeValueVO> list) {
        this.code = code;
        this.list = list;
    }

    public static String of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (ChangeOperationEnum obj : ChangeOperationEnum.values()) {
            for (CodeValueVO vo : obj.getList()) {
                if (vo.getCode().equals(code)) {
                    return vo.getValue();
                }
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public ImmutableList<CodeValueVO> getList() {
        return list;
    }
}
