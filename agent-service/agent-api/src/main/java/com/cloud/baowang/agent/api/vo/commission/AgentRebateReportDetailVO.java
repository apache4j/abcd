package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/03 22:22
 */
@Data
@Schema(title = "返点详情")
public class AgentRebateReportDetailVO {
    @Schema(title = "id")
    private String id;

    @Schema(title = "返点表ID")
    private String rebateReportId;

    @Schema(title = "场馆类型")
    private String venueType;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "有效流水")
    private String validAmount;

    @Schema(title = "返点比例")
    private BigDecimal rebateRate;

    @Schema(title = "返点金额")
    private BigDecimal rebateAmount;
}
