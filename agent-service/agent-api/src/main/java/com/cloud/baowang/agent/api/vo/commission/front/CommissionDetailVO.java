package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:13
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "佣金明细", description = "佣金明细")
public class CommissionDetailVO {
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "有效活跃")
    private Integer activeValidNumber;

    @Schema(description = "有效新增")
    private Integer newActiveNumber;

    @Schema(description = "总输赢")
    private BigDecimal userWinLossTotal;

    @Schema(description = "有效流水")
    private BigDecimal validBetAmount;

    @Schema(description = "场馆费")
    private BigDecimal venueFee;

    @Schema(description = "活动优惠")
    private BigDecimal discountAmount;

    @Schema(description = "已使用优惠")
    private BigDecimal discountUsed;

    @Schema(description = "vip福利")
    private BigDecimal vipAmount;

    @Schema(description = "总存取手续费")
    private BigDecimal accessFee;

    @Schema(description = "净输赢")
    private BigDecimal netWinLoss;

    @Schema(description = "待冲正金额")
    private BigDecimal lastMonthRemain;

    @Schema(description = "佣金")
    private BigDecimal commissionAmount;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "佣金调整金额")
    private BigDecimal reviewAdjustAmount;

    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;

    @Schema(description = "会员输赢")
    private BigDecimal betWinLoss;
}
