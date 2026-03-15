package com.cloud.baowang.agent.api.vo.withdrawConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "代理提款配置返回VO")
public class AgentWithdrawConfigVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "配置详情")
    private List<AgentWithdrawConfigDetailVO> detailList;

}
