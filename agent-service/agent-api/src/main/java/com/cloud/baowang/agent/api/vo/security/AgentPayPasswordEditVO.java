package com.cloud.baowang.agent.api.vo.security;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "支付密码编辑对象")
public class AgentPayPasswordEditVO {

    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(description = "站点编码", hidden = true)
    private String siteCode;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;
}
