package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理存提总计")
public class ReportRechargeAgentVO {
    @Schema(description = "代理ID")
    private String agentId;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "存取款方式id")
    private String depositWithdrawWayId;
    @Schema(description = "充值金额")
    private BigDecimal rechargeAmount = BigDecimal.ZERO;
    @Schema(description = "法币充值金额")
    private BigDecimal fiatRechargeAmount = BigDecimal.ZERO;
    @Schema(description = "提款金额")
    private BigDecimal withdrawAmount = BigDecimal.ZERO;
    @Schema(description = "法币提款金额")
    private BigDecimal fiatWithdrawAmount = BigDecimal.ZERO;
    @Schema(description = "结算手续费")
    private BigDecimal settlementFeeAmount = BigDecimal.ZERO;
}
