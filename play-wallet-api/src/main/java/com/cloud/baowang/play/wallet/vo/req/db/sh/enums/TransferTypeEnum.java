package com.cloud.baowang.play.wallet.vo.req.db.sh.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum TransferTypeEnum {

    // 扣款类（负数）
    DEDUCTION_ANCHOR("DEDUCTION_ANCHOR", "主播打赏(负数，需要扣款)"),
    DEDUCTION_DEADLER("DEDUCTION_DEADLER", "荷官打赏(负数，需要扣款)"),
    DEDUCTION_COMPETE_REGISTERY("DEDUCTION_COMPETE_REGISTERY", "百家乐大赛报名费扣款(负数，需要扣款)"),
    DEDUCTION_COMPETE_INITAMOUNT("DEDUCTION_COMPETE_INITAMOUNT", "百家乐大赛初始化筹码扣款(负数，需要扣款)"),
    DEDUCTION_COMPETE_BUYMORE("DEDUCTION_COMPETE_BUYMORE", "百家乐大赛增购费扣款(负数，需要扣款)"),
    DEDUCTION_COMPETE_REPEAT("DEDUCTION_COMPETE_REPEAT", "百家乐大赛重购费扣款(负数，需要扣款)"),
    DEDUCTION_COMPETE_BUY_JETTON_FEE("DEDUCTION_COMPETE_BUY_JETTON_FEE", "百家乐大赛购买筹码费(负数，需要扣款)"),

    // 派彩类
    PAYOUT_RED_ENVELOPE("PAYOUT_RED_ENVELOPE", "红包雨活动派彩"),
    PAYOUT_COMPETE("PAYOUT_COMPETE", "百家乐大赛派彩"),
    PAYOUT_COMPETE_REGISTERY("PAYOUT_COMPETE_REGISTERY", "百家乐大赛退赛退还报名费"),
    PAYOUT_COMPETE_INITAMOUNT("PAYOUT_COMPETE_INITAMOUNT", "百家乐大赛退赛退还初始化筹码"),
    PAYOUT_COMPETE_BUY_JETTON("PAYOUT_COMPETE_BUY_JETTON", "百家乐大赛退回购买筹码"),
    PAYOUT_SETTLEMENT("PAYOUT_SETTLEMENT", "百家乐大赛结算"),

    // 回滚类
    ROLLBACK_ANCHOR("ROLLBACK_ANCHOR", "主播打赏回滚"),
    ROLLBACK_DEADLER("ROLLBACK_DEADLER", "荷官打赏回滚"),
    ROLLBACK_COMPETE_REGISTERY("ROLLBACK_COMPETE_REGISTERY", "百家乐大赛报名费回滚"),
    ROLLBACK_COMPETE_INITAMOUNT("ROLLBACK_COMPETE_INITAMOUNT", "百家乐大赛初始化筹码回滚"),
    ROLLBACK_COMPETE_BUYMORE("ROLLBACK_COMPETE_BUYMORE", "百家乐大赛增购费回滚"),
    ROLLBACK_COMPETE_REPEAT("ROLLBACK_COMPETE_REPEAT", "百家乐大赛重购费回滚"),
    ROLLBACK_COMPETE_BUY_JETTON_FEE("ROLLBACK_COMPETE_BUY_JETTON_FEE", "百家乐大赛购买筹码回滚"),

    // 额外奖励类
    PAYOUT_POINT_ACTIVITY("PAYOUT_POINT_ACTIVITY", "玩法返利活动派彩"),
    PAYOUT_TASK_REWARD("PAYOUT_TASK_REWARD", "任务奖励"),
    PAYOUT_LOTTERY_REWARD("PAYOUT_LOTTERY_REWARD", "抽奖活动"),
    PAYOUT_REDEEM_REWARD("PAYOUT_REDEEM_REWARD", "兑奖奖励"),
    PAYOUT_MANUAL_POSITIVE("PAYOUT_MANUAL_POSITIVE", "手动冲正"),
    UNKNOWN("UNKNOWN","未知")
    ;


    private final String code;
    private final String desc;

    public static TransferTypeEnum fromCode(String code) {
        for (TransferTypeEnum e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}

