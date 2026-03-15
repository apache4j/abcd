package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description ="代理分配日志请求vo")
public class AgentDistributeLogReqVO extends PageVO {

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "账户类型")
    private Integer accountType;

    @Schema(description = "搜索用户名")
    private String searchAccount;

    @NotNull(message = "转账类型不能为空")
    private Integer transferType;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-开始时间")
    private Long startTime;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-结束时间")
    private Long endTime;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
}
