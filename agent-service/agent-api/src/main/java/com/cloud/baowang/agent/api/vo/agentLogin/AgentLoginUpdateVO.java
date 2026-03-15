package com.cloud.baowang.agent.api.vo.agentLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "更新代理 Request")
public class AgentLoginUpdateVO {

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理id")
    private String agentId;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "离线天数")
    private Integer offlineDays;

    private Long updatedTime;

    @Schema(title = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;
}
