package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理验证码校验请求对象")
public class AgentVerifyCodeVO implements Serializable {
    @Schema(description = "代理账号")
    @NotEmpty(message = "代理账号不能为空")
    private String agentAccount;

    @Schema(description = "邮箱")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String email;

    @Schema(title = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String verifyCode;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
