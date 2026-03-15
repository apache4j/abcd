package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "代理提款记录统计类")
@Data
public class AgentWithdrawalStatisticsVO {

    @Schema(description = "总申请金额", example = "1,000,000.00")
    private BigDecimal totalRequestedAmount;

    @Schema(description = "申请金额币种", example = "USD")
    private String totalRequestedAmountCurrencyCode;

    @Schema(description = "总下分金额-平台币", example = "1,000,000.00 BCD")
    private BigDecimal totalDistributedAmount;

    @Schema(description = "总下分金额币种", example = "WTC")
    private String totalDistributedAmountCurrencyCode;

    @Schema(description = "总订单", example = "1000")
    private Integer totalOrders;

    @Schema(description = "申请中", example = "1000")
    private Integer applicationsInProgress;

    @Schema(description = "成功", example = "1000")
    private Integer successfulWithdrawals;

    @Schema(description = "成功率", example = "80.00")
    private BigDecimal successRate;

    @Schema(description = "失败", example = "1000")
    private Integer failedWithdrawals;

    @Schema(description = "出款中")
    private Integer withdrawalsInProgress;


}
