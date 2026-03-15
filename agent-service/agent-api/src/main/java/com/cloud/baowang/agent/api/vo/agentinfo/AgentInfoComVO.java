package com.cloud.baowang.agent.api.vo.agentinfo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 8:23
 * */
@Data
@Schema(title = "代理通用请求对象")
public class AgentInfoComVO {

    @NotEmpty(message = "代理账号不能为空")
    @Schema(title = "代理账号")
    private String agentAccount;
}
