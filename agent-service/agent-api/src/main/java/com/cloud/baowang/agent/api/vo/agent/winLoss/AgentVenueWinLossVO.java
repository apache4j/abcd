package com.cloud.baowang.agent.api.vo.agent.winLoss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 11:01
 * @description: 代理场馆盈亏
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentVenueWinLossVO {
    @Schema(description = "venueCode")
    private String venueCode;
    @Schema(description = "货币")
    private String currency;
    @Schema(description = "有效投注")
    private BigDecimal validAmount;
    @Schema(description = "有效投注")
    private BigDecimal validBetPlatAmount;
    @Schema(description = "平台输赢 投注输赢-打赏金额")
    private BigDecimal winLossAmount;
    @Schema(description = "会员输赢 原始投注输赢")
    private BigDecimal userWinLossAmount;

    private String siteCode;
}
