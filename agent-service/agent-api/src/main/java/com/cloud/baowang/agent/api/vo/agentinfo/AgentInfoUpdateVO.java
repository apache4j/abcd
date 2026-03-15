package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 代理更新信息对象
 * </p>
 */
@Data
@Schema(title = "UpdateVO对象", description = "代理更新信息对象")
public class AgentInfoUpdateVO  {

    private String id;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "代理契约模式-佣金契约 1是 0否")
    private Integer contractModelCommission;

    @Schema(description = "代理契约模式-返点契约 1是 0否")
    private Integer contractModelRebate;

    @Schema(description = "契约状态 1已签约 0未签约")
    private Integer contractStatus;

    @Schema(description = "备注信息")
    private String remark;
}
