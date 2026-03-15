package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 21:36
 * @description: 代理登录请求对象
 * */
@Data
@Schema(title = "代理登录请求对象")
public class AgentLoginParamVO {

    @NotEmpty(message = "代理账号不能为空")
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "用戶密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(title = "验证码")
    private String verifyCode;

    @Schema(title = "验证码KEY")
    private String codeKey;

    @Schema(title = "设备号")
    private String deviceNo;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

}
