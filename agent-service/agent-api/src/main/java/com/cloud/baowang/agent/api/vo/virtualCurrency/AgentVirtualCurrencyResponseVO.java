package com.cloud.baowang.agent.api.vo.virtualCurrency;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**

 */
@Data
@Schema(title = "代理虚拟币账号管理 返回")
public class AgentVirtualCurrencyResponseVO {

    @Schema(description =  "id")
    private Long id;

    @Schema(description =  "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description =  "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description =  "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(description =  "黑名单状态 0禁用 1启用")
    private Integer blackStatus;
    @Schema(description =  "黑名单状态 0禁用 1启用")
    private String blackStatusName;

    @Schema(description =  "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;
    @Schema(description =  "绑定状态 0未绑定 1绑定中")
    private String bindingStatusName;

    @Schema(description =  "风控层级id")
    private String riskControlLevelId;
    @Schema(description =  "风控层级")
    private String riskControlLevel;

    @Schema(description =  "绑定账号数量")
    private Integer bindingAccountTimes;


    @Schema(description =  "当前绑定代理账号")
    private String currentBindingAgentAccount;

    @Schema(description =  "代理姓名")
    private String agentName;


    @Schema(description =  "代理提款成功次数")
    private Integer agentWithdrawSuccessTimes;

    @Schema(description =  "代理提款被拒次数")
    private Integer agentWithdrawFailTimes;

    @Schema(description =  "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(description =  "虚拟币账号新增时间")
    private Long firstUseTime;

    @Schema(description =  "最近提款时间")
    private Long lastWithdrawTime;

    @Schema(description =  "最近操作人")
    private String lastOperator;

    @Schema(description =  "最近操作时间")
    private Long updatedTime;
}
