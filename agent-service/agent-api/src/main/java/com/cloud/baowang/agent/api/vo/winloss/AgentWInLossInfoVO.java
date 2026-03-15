package com.cloud.baowang.agent.api.vo.winloss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @Author : 小智
 * @Date : 26/10/23 10:46 AM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理盈亏明细返回对象")
public class AgentWInLossInfoVO implements Serializable {

    @Schema(description = "币种")
    private String mainCurrency;

    @Schema(description ="会员总投注 币种是主货币")
    private BigDecimal totalBetAmount;

     @Schema(description ="会员有效投注 币种是主货币")
    private BigDecimal validAmount;

     @Schema(description ="会员游戏盈亏 币种是主货币")
    private BigDecimal betWinLoss;

     @Schema(description ="会员总盈亏 币种是主货币")
    private BigDecimal totalWinLoss;

     @Schema(description ="会员总返水 币种是主货币 字段已删除")
    private BigDecimal totalRebate;

     @Schema(description ="会员总优惠 币种为平台币")
    private BigDecimal totalDiscount;

     @Schema(description ="会员账户调整 币种是主货币")
    private BigDecimal accountAdjust;

     @Schema(description ="总场馆费 字段已删除")
    private BigDecimal totalVenueAmount;

     @Schema(description ="代理总返点 字段已删除")
    private BigDecimal agentRebateAmount;

     @Schema(description ="代理净输赢 字段已删除")
    private BigDecimal agentWinLoss;

     @Schema(description ="代理上月结余 字段已删除")
    private BigDecimal agent;

    public BigDecimal getTotalBetAmount() {
        return Optional.ofNullable(totalBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValidAmount() {
        return Optional.ofNullable(validAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getBetWinLoss() {
        return Optional.ofNullable(betWinLoss).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalWinLoss() {
        return Optional.ofNullable(totalWinLoss).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalRebate() {
        return Optional.ofNullable(totalRebate).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalDiscount() {
        return Optional.ofNullable(totalDiscount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAccountAdjust() {
        return Optional.ofNullable(accountAdjust).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalVenueAmount() {
        return Optional.ofNullable(totalVenueAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAgentRebateAmount() {
        return Optional.ofNullable(agentRebateAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAgentWinLoss() {
        return Optional.ofNullable(agentWinLoss).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAgent() {
        return Optional.ofNullable(agent).orElse(BigDecimal.ZERO);
    }
}
