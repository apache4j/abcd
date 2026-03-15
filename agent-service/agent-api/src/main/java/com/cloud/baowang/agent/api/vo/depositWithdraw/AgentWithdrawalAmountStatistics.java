package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "统计类")
public class AgentWithdrawalAmountStatistics {

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "提款金额")
    private BigDecimal applyAmount;

    @Schema(title = "提款币种金额")
    private BigDecimal arriveAmount;
    @Schema(title = "提款币种金额")
    private String tradeCurrencyAmountCurrencyCode;

    /**
     * 手续费
     */
    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "币种")
    private String currencyCode;



}
