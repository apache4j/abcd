package com.cloud.baowang.agent.api.vo.withdrawConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "代理提款配置 新增入参")
public class AgentWithdrawConfigAddVO {

    @Schema(title = "站点编号",hidden = true)
    private String siteCode;

    @Schema(title = "代理账号")
    @NotEmpty(message = "代理账号不能为空")
    private String agentAccount;

    @Schema(title = "状态 1开启 0关闭")
    @NotNull(message = "开关状态不能为空")
    private Integer status;

    private List<AgentWithdrawDetailRspVO> configList;
}
