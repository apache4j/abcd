package com.cloud.baowang.agent.api.vo.security;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理发送邮箱验证码请求对象")
public class AgentGetMailCodeVO implements Serializable {
    @Schema(description = "邮箱")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String email;
    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
}
