package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "代理密保问题验证对象")
public class AgentSecurityVerifyVO {
    private List<AgentSecurityVO> agentSecurityList;

    @Schema(description = "代理账号")
    private String agentAccount;


    @Schema(description = "站点编号")
    private String siteCode;
}
