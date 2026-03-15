package com.cloud.baowang.agent.api.vo.agent.winLoss;

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
@Schema(description = "代理盈亏对象")
public class AgentWinLoseVO implements Serializable {


    @Schema(description = "会员总投注")
    private BigDecimal betAmount;

    @Schema(description = "会员有效投注")
    private BigDecimal validAmount;

    @Schema(description = "会员游戏盈亏")
    private BigDecimal betWinLose;

    @Schema(description = "会员总返水")
    private BigDecimal rebateAmount;

    @Schema(title = "活动优惠金额 单位平台币")
    private BigDecimal activityAmount;

    @Schema(title = "已使用优惠金额 单位主货币")
    private BigDecimal  alreadyUseAmount;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount;

    @Schema(title = "调整金额(其他调整)-主货币")
    private BigDecimal adjustAmount;

    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount;

    @Schema(description = "总注单量")
    private Integer betNum;

    @Schema(description = "总投注人数")
    private Integer betUserNum;

    @Schema(description = "币种")
    private String currency;

    private String agentAccount;

    public BigDecimal getBetAmount() {
        return Optional.ofNullable(betAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValidAmount() {
        return Optional.ofNullable(validAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getBetWinLose() {
        return Optional.ofNullable(betWinLose).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getRebateAmount() {
        return  Optional.ofNullable(rebateAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getActivityAmount() {
        return  Optional.ofNullable(activityAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getVipAmount() {
        return  Optional.ofNullable(vipAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAdjustAmount() {
        return  Optional.ofNullable(adjustAmount).orElse(BigDecimal.ZERO);
    }

}
