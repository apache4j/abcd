package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "登录密码修改对象")
public class AgentPasswordSetVO {

    @Schema(description = "代理账号")
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
