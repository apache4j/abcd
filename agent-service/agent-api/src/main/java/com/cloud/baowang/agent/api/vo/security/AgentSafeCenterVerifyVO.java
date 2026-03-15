package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "安全中心代理密保问题验证对象")
public class AgentSafeCenterVerifyVO {
    private List<AgentSecurityVO> agentSecurityList;

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "站点编号", hidden = true)
    private String siteCode;
}
