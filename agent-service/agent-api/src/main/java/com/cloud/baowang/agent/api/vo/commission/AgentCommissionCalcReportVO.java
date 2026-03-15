package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class AgentCommissionCalcReportVO {

    @Schema(description ="siteCode")
    private String siteCode;
    @Schema(description ="代理ID")
    private String agentId;

    private BigDecimal commissionAmount;
}
