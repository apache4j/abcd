package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class AgentCommissionReportQueryVO {

    @Schema(description ="siteCode",hidden = true)
    private String siteCode;

    private String agentId;


    @Schema(description ="startTime")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description ="endTime")
    private Long endTime;

}
