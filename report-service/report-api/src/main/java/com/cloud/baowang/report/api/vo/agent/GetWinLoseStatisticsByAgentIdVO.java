package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理客户端-总输赢 按天统计 VO")
public class GetWinLoseStatisticsByAgentIdVO implements Serializable {

    @Schema(description = "日期")
    private String myDay;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "输赢金额总和")
    private BigDecimal betWinLose;

    @Schema(description = "有效投注金额总和")
    private BigDecimal validBetAmount;
}
