package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "绑定身份验证器")
public class AgentBindAuthenticatorVO {


    @Schema(description = "google密钥")
    private String googleAuthKey;

    @Schema(description = "验证码")
    private String verifyCode;

    @Schema(description = "登录密码")
    private String password;

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "站点编码", hidden = true)
    private String siteCode;

    @Schema(description = "是否是重新绑定", hidden = true)
    private Boolean rebind;
}
