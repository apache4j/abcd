package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@Schema(title = "代理财务信息请求对象")
public class AgentFinanceRequestVO {

    @Schema(description = "代理账号")
    @NotBlank(message = "代理账号不能为空")
    private String agentAccount;


    private String siteCode;

}
