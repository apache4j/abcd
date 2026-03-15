package com.cloud.baowang.agent.api.vo.security;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2023/10/20 8:23
 * @description: 代理密保登录
 * */
@Data
@Schema(description = "代理密保登录请求对象")
public class AgentSecurityLoginParamVO {

    @NotEmpty(message = "代理账号不能为空")
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "用戶密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(description = "终端设备号")
    private String deviceNo;

    @Schema(description = "密保问题列表")
    private List<AgentSecurityVO> agentSecurityList;

}
