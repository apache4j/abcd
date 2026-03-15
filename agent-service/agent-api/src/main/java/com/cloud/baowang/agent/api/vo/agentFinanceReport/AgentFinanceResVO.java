package com.cloud.baowang.agent.api.vo.agentFinanceReport;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理财务报表-返回包含个人与团队财务")
public class AgentFinanceResVO {
    @Schema(description = "个人财务")
    private AgentFinanceCurrencyResVO currFinanceVO;
    @Schema(description = "团队财务")
    private AgentFinanceCurrencyResVO teamFinanceVO;
}
