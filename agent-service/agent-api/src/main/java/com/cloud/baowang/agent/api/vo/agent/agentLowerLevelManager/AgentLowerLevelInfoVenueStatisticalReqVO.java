package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgentLowerLevelInfoVenueStatisticalReqVO {
    @NotBlank
    @Schema(description ="下级代理账号")
    private String agentAccount;
    @NotNull
    @Schema(description ="统计开始时间")
    private Long startTime;
    @NotNull
    @Schema(description ="统计结束时间")
    private Long endTime;
}
