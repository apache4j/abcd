package com.cloud.baowang.report.api.vo.venuewinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理下场馆费计算返回")
public class ReportVenueWinLossAgentVO {
    @Schema(description = "代理ID")
    private String agentId;
    @Schema(description = "场馆code")
    private String venueCode;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "输赢金额")
    private BigDecimal winLoseAmount;
    @Schema(description = "有效投注金额")
    private BigDecimal validBetAmount;
    @Schema(description = "手续费")
    private BigDecimal feeAmount;
    @Schema(description = "手续费率")
    private BigDecimal feeRate;

    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;
}
