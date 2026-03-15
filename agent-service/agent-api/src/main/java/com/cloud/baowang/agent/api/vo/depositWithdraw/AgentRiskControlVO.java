package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.enums.RiskTypeEnum;
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
@Schema(title = "代理账号风控层级")
public class AgentRiskControlVO {

    @Schema(description = "风控代理")
    private String riskAgent;

    @Schema(title = "风险银行卡")
    private String riskCard;

    @Schema(title = "风险虚拟币")
    private String riskVirtualCurrency;

    @Schema(title = "风险IP")
    private String riskIp;

    @Schema(title = "风险终端设备号")
    private String riskTerminal;
    @Schema(description = "风控电子钱包")
    private String riskWallet;
}
