package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 11:01
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "负盈利佣金", description = "负盈利佣金")
public class CommissionGeneralVO {
    @Schema(description = "负盈利佣金")
    private BigDecimal commissionAmount = new BigDecimal("0.0000");

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;

    @Schema(description = "计算开始时间")
    private Long startTime;

    @Schema(description = "计算结束时间")
    private Long endTime;

    @Schema(description = "有效活跃")
    private Integer activeValidNumber = 0;

    @Schema(description = "有效新增")
    private Integer newActiveNumber = 0;

    @Schema(description = "总输赢")
    private BigDecimal userWinLossTotal = new BigDecimal("0.0000");

    @Schema(description = "已使用优惠")
    private BigDecimal discountUsed = new BigDecimal("0.0000");

    @Schema(description = "待冲正金额")
    private BigDecimal lastMonthRemain = new BigDecimal("0.0000");

    @Schema(description = "总存取手续费")
    private BigDecimal accessFee = new BigDecimal("0.0000");

    @Schema(description = "场馆费")
    private BigDecimal venueFee = new BigDecimal("0.0000");

    @Schema(description = "返佣比例")
    private BigDecimal agentRate  = new BigDecimal("0.0000");

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount = new BigDecimal("0.0000");

    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount = new BigDecimal("0.0000");

    @Schema(description = "会员输赢")
    private BigDecimal betWinLoss = new BigDecimal("0.0000");


    public BigDecimal getCommissionAmount() {
        return commissionAmount.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getVenueFee() {
        return venueFee.setScale(4, RoundingMode.DOWN);
    }

    public BigDecimal getAccessFee() {
        return accessFee.setScale(4, RoundingMode.DOWN);
    }
}
