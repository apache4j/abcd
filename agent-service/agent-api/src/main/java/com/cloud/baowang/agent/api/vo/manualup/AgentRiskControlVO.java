package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 账号风控层级
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
 @Schema(description ="账号风控层级")
public class AgentRiskControlVO {

     @Schema(description ="风险代理")
    private String riskAgent;

     @Schema(description ="风险银行卡")
    private String riskCard;

     @Schema(description ="风险虚拟币")
    private String riskVirtualCurrency;

     @Schema(description ="风险IP")
    private String riskIp;

     @Schema(description ="风险终端设备号")
    private String riskTerminal;
}
