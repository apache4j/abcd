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
@Schema(description = "代理找回密码-修改密码")
public class AgentResetPasswordVO implements Serializable {
    @Schema(description = "代理账号",hidden = true)
    private String agentAccount;

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;



}
