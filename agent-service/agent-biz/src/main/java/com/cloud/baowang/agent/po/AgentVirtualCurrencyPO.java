package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理虚拟币地址信息
 * </p>
 *
 * @author qiqi
 * @since 2023-10-11
 */
@Data
@TableName("agent_virtual_currency")
@Schema(title = "AgentVirtualCurrency对象", description = "代理虚拟币地址信息")
public class AgentVirtualCurrencyPO extends BasePO {

    private static final long serialVersionUID = 1L;


    @Schema(description = "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description = "虚拟币账号地址-别名")
    private String virtualCurrencyAddressAlias;

    @Schema(description = "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description = "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(description = "黑名单状态 0禁用 1启用")
    private Integer blackStatus;

    @Schema(description = "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;

    @Schema(description = "风控层级id")
    private String riskControlLevelId;

    @Schema(description = "风控层级")
    private String riskControlLevel;

    @Schema(description = "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(description = "当前绑定代理id")
    private String currentBindingAgentId;

    @Schema(description = "当前绑定代理账号")
    private String currentBindingAgentAccount;

    @Schema(description = "代理类型 1正式 2商务 3置换")
    private String agentType;

    @Schema(description = "代理提款成功次数")
    private Integer agentWithdrawSuccessTimes;

    @Schema(description = "代理提款被拒次数")
    private Integer agentWithdrawFailTimes;

    @Schema(description = "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(description = "虚拟币账号新增时间")
    private Long firstUseTime;

    @Schema(description = "最近提款时间")
    private Long lastWithdrawTime;

    @Schema(description = "最近操作人")
    private String lastOperator;

}
