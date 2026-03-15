package com.cloud.baowang.wallet.api.vo.uservirtualcurrency;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 风控编辑获取风险虚拟币VO
 */
@Data
@Schema(title = "风控编辑获取风险虚拟币VO")
public class RiskEditVirtualCurrencyAddressVO {

    @Schema(title =  "主键id")
    private String id;

    @Schema(title =  "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(title =  "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(title =  "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(title =  "黑名单状态 0禁用 1启用")
    private Integer blackStatus;

    @Schema(title =  "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;


    @Schema(title =  "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(title =  "会员提款总金额")
    private BigDecimal userWithdrawSumAmount;

    @Schema(title =  "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(title =  "风控层级id")
    private Long riskControlLevelId;
    @Schema(title =  "风控层级")
    private String riskControlLevel;
}
