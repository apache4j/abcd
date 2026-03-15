package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/11 16:36
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "代理存取款汇总对象")
public class ReportAgentAmountVO {
    @Schema(description = "agentId")
    private String agentId;
    @Schema(description = "存款金额")
    private BigDecimal rechargeAmount;
    @Schema(description = "取款金额")
    private BigDecimal withdrawAmount;
    @Schema(description = "方式手续费")
    private BigDecimal wayFeeAmount;
    @Schema(description = "币种")
    private String currency;
}
