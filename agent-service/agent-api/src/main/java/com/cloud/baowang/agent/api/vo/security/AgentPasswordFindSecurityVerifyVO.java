package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "代理密码找回安全验证对象")
public class AgentPasswordFindSecurityVerifyVO {

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "代理用户ID")
    private List<AgentSecurityVO> agentSecurityVOList;

    @Schema(description = "验证类型；1.密保；2.谷歌；3.电子邮箱")
    private Integer type;

    @Schema(description = "验证码")
    private String verifyCode;
}
