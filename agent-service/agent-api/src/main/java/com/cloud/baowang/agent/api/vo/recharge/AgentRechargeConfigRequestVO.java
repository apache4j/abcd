package com.cloud.baowang.agent.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理充值配置信息请求")
public class AgentRechargeConfigRequestVO {


    /**
     * 充值方式ID
     */
    @Schema(description = "充值方式ID")
    private String rechargeWayId;


    private String agentAccount;

    private String siteCode;

    private String agentId;
}
