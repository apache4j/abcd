package com.cloud.baowang.agent.api.vo.agentLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/11/06 21:36
 * @description:
 * */
@Data
@Schema(title = "代理账号验证响应VO")
public class AgentAccountCheckResVO {

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "是否已设置google密钥, true 已设置  false 未设置")
    private Boolean isSetGoogle;
}
