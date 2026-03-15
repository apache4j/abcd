package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理下支付方式会员存提总计")
public class ReportUserRechargePayMethodAgentVO {
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "支付方式 id")
    private String payMethodId;
    @Schema(description = "存/取 金额")
    private BigDecimal amount = BigDecimal.ZERO;
    @Schema(description = "结算手续费")
    private BigDecimal settlementFeeAmount = BigDecimal.ZERO;
}
