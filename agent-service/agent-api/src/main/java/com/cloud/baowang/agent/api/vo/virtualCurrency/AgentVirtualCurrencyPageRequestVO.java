package com.cloud.baowang.agent.api.vo.virtualCurrency;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代理虚拟币账号管理 Request
 */
@Data
@Schema(title = "代理虚拟币账号管理 Request")
public class AgentVirtualCurrencyPageRequestVO extends PageVO {

    @Schema(description = "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description = "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description = "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(description = "黑名单状态 0禁用 1启用")
    private Integer blackStatus;

    @Schema(description = "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;

    @Schema(description = "最近操作人")
    private String lastOperator;

    @Schema(description = "当前绑定代理账号")
    private String currentBindingAgentAccount;

    @Schema(description = "代理姓名")
    private String agentName;

    @Schema(description = "风控层级id")
    private List<Long> riskControlLevelId;

    @Schema(description = "代理提款被拒次数-最小值")
    private Integer agentWithdrawFailTimesMin;

    @Schema(description = "代理提款被拒次数-最大值")
    private Integer agentWithdrawFailTimesMax;

    @Schema(description = "代理提款成功次数-最小值")
    private Integer agentWithdrawSuccessTimesMin;

    @Schema(description = "代理提款成功次数-最大值")
    private Integer agentWithdrawSuccessTimesMax;

    @Schema(description = "代理提款总金额-最小值")
    private BigDecimal agentWithdrawSumAmountMin;

    @Schema(description = "代理提款总金额-最大值")
    private BigDecimal agentWithdrawSumAmountMax;

    @Schema(description = "绑定账号数量")
    private Integer bindingAccountTimes;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;
}
