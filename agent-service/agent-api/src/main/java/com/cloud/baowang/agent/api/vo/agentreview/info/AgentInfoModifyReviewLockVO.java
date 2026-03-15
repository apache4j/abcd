package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "代理信息修改审核锁单入参")
public class AgentInfoModifyReviewLockVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "操作人", hidden = true)
    private String operator;


    @NotNull
    @Schema(description = "id")
    private String id;
}
