package com.cloud.baowang.play.api.vo.pt2.vo.settle;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameRoundSummary {

//    基础游戏中的下注金额
    private BigDecimal baseBetAmount;

    //基础游戏中的获胜金额（不包括奖金和赌博回合下注）
    private BigDecimal baseWinAmount;

    //发生奖励回合的次数
    private Long bonusRoundCount;

    //奖励回合的获胜金额。
    private BigDecimal bonusRoundWinAmount;

    //发生赌博回合的次数
    private Long gambleRoundCount;

    //赌博回合的下注金额
    private BigDecimal gambleRoundBetAmount;

    //赌博回合的获胜金额
    private BigDecimal gambleRoundWinAmount;
}
