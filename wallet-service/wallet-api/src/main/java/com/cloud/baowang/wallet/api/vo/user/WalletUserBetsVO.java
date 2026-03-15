package com.cloud.baowang.wallet.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "投注信息")
public class WalletUserBetsVO {

    @Schema(description ="总投注")
    private BigDecimal totalBetsAmount;

    @Schema(description ="总有效投注金额")
    private BigDecimal validBetAmount;

    @Schema(description ="投注输赢")
    private BigDecimal playerWinLose;

    @Schema(description ="红利")
    private BigDecimal activityAmount;

    @Schema(description ="返水金额")
    private BigDecimal rebateAmount;

    @Schema(description ="vip金额",hidden = true)
    private BigDecimal vipAmount;

    @Schema(description ="已使用优惠",hidden = true)
    private BigDecimal alreadyUseAmount;

    @Schema(description ="调整金额",hidden = true)
    private BigDecimal adjustAmount;

    @Schema(description ="打赏金额")
    private BigDecimal tipsAmount;

    @Schema(description = "主货币币种")
    private String currency;

    @Schema(description = "平台币币种")
    private String platformCurrency;

    @Schema(description = "净输赢")
    private BigDecimal profitAndLoss;

    @Schema(description = "风控金额")
    private BigDecimal riskAmount;

    @Schema(description = "平台币调整金额（其他）")
    private BigDecimal platAdjustAmount;

    public BigDecimal getRiskAmount() {
        return Optional.ofNullable(riskAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTipsAmount() {
        return Optional.ofNullable(tipsAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalBetsAmount() {
        return Optional.ofNullable(totalBetsAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getPlayerWinLose() {
        return Optional.ofNullable(playerWinLose).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getActivityAmount() {
        return Optional.ofNullable(activityAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getRebateAmount() {
        return Optional.ofNullable(rebateAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCompanyWinLose() {
        return Optional.ofNullable(companyWinLose).orElse(BigDecimal.ZERO);
    }

    @Schema(description ="净盈利")
    private BigDecimal companyWinLose;




}
