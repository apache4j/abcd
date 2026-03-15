package com.cloud.baowang.agent.api.vo.winloss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理汇总下级盈亏对象")
public class AgentMonthWinLoseVO implements Serializable {


    @Schema(description = "会员总投注")
    private BigDecimal totalBetAmount;

    @Schema(description = "会员有效投注")
    private BigDecimal totalValidAmount;

    @Schema(description = "会员游戏盈亏")
    private BigDecimal totalBetWinLose;

    @Schema(description = "会员总返水")
    private BigDecimal totalRebate;

    @Schema(description = "会员总优惠")
    private BigDecimal totalDiscountAmount;

    @Schema(description = "会员账户调整")
    private BigDecimal totalAdjustAmount;

    @Schema(description = "会员补单其他调整")
    private BigDecimal totalOtherAdjustAmount;

    @Schema(description = "总注单量")
    private Integer betNum;

    @Schema(description = "总投注人数")
    private Integer betUserNum;

    private String agentAccount;

    public BigDecimal getTotalBetAmount() {
        return Optional.ofNullable(totalBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalValidAmount() {
        return Optional.ofNullable(totalValidAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalBetWinLose() {
        return Optional.ofNullable(totalBetWinLose).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalRebate() {
        return Optional.ofNullable(totalRebate).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalDiscountAmount() {
        return Optional.ofNullable(totalDiscountAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalAdjustAmount() {
        return Optional.ofNullable(totalAdjustAmount).orElse(BigDecimal.ZERO);
    }
}
