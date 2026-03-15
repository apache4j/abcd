package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description ="下级代理详情req")
public class AgentLowerLevelManagerInfoReqVO {
    @NotBlank(message = "下级代理账号不能为空")
    @Schema(description ="下级代理账号")
    private String agentAccount;
    @NotNull(message = "统计开始时间不能为空")
    @Schema(description ="统计开始时间")
    private Long startTime;
    @NotNull(message = "统计结束时间不能为空")
    @Schema(description ="统计结束时间")
    private Long endTime;
    @Schema(description ="货币选择")
    private String currencyCode;
}
