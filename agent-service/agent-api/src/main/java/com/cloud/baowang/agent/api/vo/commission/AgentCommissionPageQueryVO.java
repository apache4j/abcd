package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AgentCommissionPageQueryVO extends PageVO {

    @Schema(description ="siteCode",hidden = true)
    private String siteCode;
    @Schema(description ="siteCode",hidden = true)
    private String agentId;


    @Schema(description ="startTime")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description ="endTime")
    private Long endTime;

}
