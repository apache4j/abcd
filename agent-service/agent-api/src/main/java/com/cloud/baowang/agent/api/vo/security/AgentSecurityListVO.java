package com.cloud.baowang.agent.api.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理密保问题返回对象")
public class AgentSecurityListVO {

    @Schema(description = "密保问题ID")
    private String securityQuestionId;

    @Schema(description = "密保问题")
    private String securityQuestion;


}
