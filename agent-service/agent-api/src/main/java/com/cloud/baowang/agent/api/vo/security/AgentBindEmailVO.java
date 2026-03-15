package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "绑定电子邮箱")
public class AgentBindEmailVO {

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "验证码")
    private String verifyCode;

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "站点编码", hidden = true)
    private String siteCode;

}
