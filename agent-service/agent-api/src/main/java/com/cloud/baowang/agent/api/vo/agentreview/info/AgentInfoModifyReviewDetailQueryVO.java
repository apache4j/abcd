package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "代理信息修改审核查询入参")
public class AgentInfoModifyReviewDetailQueryVO {
    @NotNull
    @Schema(description = "id")
    private Long id;
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;

}
