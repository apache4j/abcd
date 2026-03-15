package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/13 8:42
 * @description:
 */
@Data
@Schema(title = "场馆输赢统计")
public class ReportAgentVenueStaticsVO {
    @Schema(description = "agentId")
    private String agentId;
    /**  {@link VenueTypeEnum}*/
    @Schema(description = "场馆类型")
    private Integer venueType;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "场馆代码")
    private String venueCode;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    private BigDecimal validBetAmount;
    @Schema(description = "有效投注平台币金额")
    private BigDecimal validBetPlatAmount;
    @Schema(description = "会员输赢")
    private BigDecimal winLossAmount;
}
