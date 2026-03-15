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
@Schema(title = "代理账号验证VO")
public class AgentAccountCheckVO {

    @NotEmpty(message = ConstantsCode.AGENT_MISSING)
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "验证码")
    @NotEmpty(message = ConstantsCode.CODE_IS_EMPTY)
    private String verifyCode;

    @Schema(title = "验证码KEY")
    private String codeKey;

}
