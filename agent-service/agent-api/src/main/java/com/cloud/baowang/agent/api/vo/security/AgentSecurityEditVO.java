package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
@Schema(description = "代理密保问题编辑对象")
public class AgentSecurityEditVO {

    private List<AgentSecurityVO> agentSecurityVOList;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "代理账号",hidden = true)
    private String agentAccount;

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

}
