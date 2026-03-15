package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "代理安全设置响应对象")
public class AgentSecuritySetVO {

    @Schema(description = "邮箱设置 1已设置 0 未设置")
    private Integer emailSet;

    @Schema(description = "手机号码设置 1已设置 0 未设置")
    private Integer phoneSet;
    @Schema(description = "密保问题")
    private List<String> securityQuestions;

    @Schema(description = "登录密码设置 1已设置 0 未设置")
    private Integer agentPasswordSet;

    @Schema(description = "支付密码设置 1已设置 0 未设置")
    private Integer payPasswordSet;

    @Schema(description = "谷歌验证秘钥 1已设置 0未设置")
    private Integer googleAuthKeySet;

}
