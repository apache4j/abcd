package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description ="代理下级管理分页查询vo")
public class AgentLowerLevelManagerPageReqVO extends PageVO {

    @Schema(description ="代理账号", hidden = true)
    private String agentAccount;

    @Schema(description ="下级账号")
    private String lowerLevelAccount;

    @NotNull(message = "开始时间不能为空")
    @Schema(description ="开始时间", required = true)
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description ="结束时间", required = true)
    private Long endTime;

    @Schema(description ="货币选择")
    private String currencyCode;

    @Schema(description ="站点编号",hidden = true)
    private String siteCode;
}
