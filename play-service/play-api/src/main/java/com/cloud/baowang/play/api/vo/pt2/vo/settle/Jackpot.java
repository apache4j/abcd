package com.cloud.baowang.play.api.vo.pt2.vo.settle;

import com.cloud.baowang.play.api.vo.pt2.vo.JackpotContribution;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Jackpot {
    //贡献到累积奖池的金额。
    private BigDecimal contributionAmount;
    //从奖池中赢得的金额
    private BigDecimal winAmount;
    //奖池id
    private String jackpotId;

    //多个奖池的贡献信息
    private List<JackpotContribution> jackpotContributions;

}
