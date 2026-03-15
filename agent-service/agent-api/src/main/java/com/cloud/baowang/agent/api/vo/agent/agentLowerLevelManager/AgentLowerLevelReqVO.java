package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description ="下级代理信息查询req")
public class AgentLowerLevelReqVO {

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "代理id", hidden = true)
    private String agentId;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "当前代理层级", hidden = true)
    private Integer level;

    @Schema(description ="下级账号")
    private String lowerLevelAccount;

    @Schema(description = "开始时间", required = true)
    private Long startTime;

    @Schema(description = "结束时间", required = true)
    private Long endTime;
}
