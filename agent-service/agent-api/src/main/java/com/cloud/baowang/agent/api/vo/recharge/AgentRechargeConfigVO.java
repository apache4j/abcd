package com.cloud.baowang.agent.api.vo.recharge;


import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="代理充值配置信息")
public class AgentRechargeConfigVO {

    /*@Schema(description = "余额")
    private BigDecimal balance;*/

    @Schema(description = "平台币汇率")
    private BigDecimal platformExchangeRate;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "充值最小值")
    private BigDecimal rechargeMinAmount;

    @Schema(description = "充值最大值")
    private BigDecimal rechargeMaxAmount;

    @Schema(description = "手续费率")
    private BigDecimal feeRate;

    @Schema(description = "是否已有三条充值中订单 0没有 1有")
    private Integer haveThreeHandingOrder;

    @Schema(description = "加密货币地址")
    private String address;

    @Schema(description = "快捷金额，逗号隔开")
    private String quickAmount;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "充值方式ID")
    private String rechargeWayId;

    @Schema(description = "充值方式")
    @I18nField
    private String rechargeWay;

    @Schema(description = "是否提醒 1提醒 0不提醒")
    private Integer isRemind;

    @Schema(description = "U最小充值金额")
    private BigDecimal uMinAmount;

    @Schema(description = "加密货币通道类型 THIRD三方  SITE_CUSTOM站点自定义")
    private String channelType;

}
